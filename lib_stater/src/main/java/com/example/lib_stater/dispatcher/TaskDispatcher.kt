package com.example.lib_stater.dispatcher

import android.app.Application
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import com.example.lib_stater.TaskStat
import com.example.lib_stater.sort.TaskSortUtil
import com.example.lib_stater.task.DispatchRunnable
import com.example.lib_stater.task.Task
import com.example.lib_stater.task.TaskCallBack
import com.example.lib_stater.utils.DispatcherLog
import com.example.lib_stater.utils.StaterUtils
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class TaskDispatcher private constructor(){
    private var mStartTime: Long = 0
    private val mFutures: MutableList<Future<*>> = ArrayList()
    private var mAllTasks: MutableList<Task> = ArrayList()
    private val mClsAllTasks: MutableList<Class<out Task>> = ArrayList()

    @Volatile
    private var mMainThreadTasks: MutableList<Task> = ArrayList()
    private var mCountDownLatch: CountDownLatch? = null

    private val mNeedWaitCount = AtomicInteger()

    private val mNeedWaitTasks: MutableList<Task> = ArrayList()

    @Volatile
    private var mFinishedTasks: MutableList<Class<out Task>> = ArrayList(100)
    private val mDependedHashMap = HashMap<Class<out Task>, ArrayList<Task>?>()

    private val mAnalyseCount = AtomicInteger()

    fun addTask(task: Task?): TaskDispatcher {
        task?.let {
            collectDepends(it)
            mAllTasks.add(it)
            mClsAllTasks.add(it.javaClass)
            if (ifNeedWait(it)) {
                mNeedWaitTasks.add(it)
                mClsAllTasks.add(it.javaClass)
            }
        }
        return this
    }

    private fun collectDepends(task: Task) {
        task.dependsOn()?.let { list ->
            for (cls in list) {
                cls?.let { cls ->
                    if (mDependedHashMap[cls] == null) {
                        mDependedHashMap[cls] = ArrayList()
                    }
                    mDependedHashMap[cls]?.add(task)
                    if (mFinishedTasks.contains(cls)) {
                        task.satisfy()
                    }
                }
            }
        }
    }

    private fun ifNeedWait(task: Task): Boolean {
        return !task.runOnMainThread() && task.needWait()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @UiThread
    fun start() {
        mStartTime = System.currentTimeMillis()
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw RuntimeException("must be called from UiThread")
        }

        if (!mAllTasks.isNullOrEmpty()) {
            mAnalyseCount.getAndIncrement()
            printDependedMsg(false)
            mAllTasks = TaskSortUtil.getSortResult(mAllTasks, mClsAllTasks).toMutableList()
            mCountDownLatch = CountDownLatch(mNeedWaitCount.get())
            sendAndExecuteAsyncTasks()
            DispatcherLog.i("task analyse cost ${(System.currentTimeMillis() - mStartTime)} begin main ")
            executeTaskMain()
        }
        DispatcherLog.i("task analyse cost startTime cost ${(System.currentTimeMillis() - mStartTime)}")
    }

    fun cancel() {
        for (future in mFutures) {
            future.cancel(true)
        }
    }

    private fun executeTaskMain() {
        mStartTime = System.currentTimeMillis()
        for (task in mMainThreadTasks) {
            val time = System.currentTimeMillis()
            DispatchRunnable(task, this).run()
            DispatcherLog.i(
                "real main ${task.javaClass.simpleName} cost ${(System.currentTimeMillis() - time)}"
            )
        }
        DispatcherLog.i("mainTask cost ${(System.currentTimeMillis() - mStartTime)}")
    }

    private fun sendAndExecuteAsyncTasks() {
        for (task in mAllTasks) {
            if (task.onlyInMainProcess() && !isMainProcess) {
                markTaskDone(task)
            } else {
                sendTaskReal(task)
            }
            task.isSend = true
        }
    }

    fun satisfyChildren(launchTask: Task) {
        val arrayList = mDependedHashMap[launchTask.javaClass]
        if (!arrayList.isNullOrEmpty()) {
            for (task in arrayList) {
                task.satisfy()
            }
        }
    }

    fun markTaskDone(task: Task) {
        if (ifNeedWait(task)) {
            mFinishedTasks.add(task.javaClass)
            mNeedWaitTasks.remove(task)
            mCountDownLatch?.countDown()
            mNeedWaitCount.getAndDecrement()
        }
    }

    private fun sendTaskReal(task: Task) {
        if (task.runOnMainThread()) {
            mMainThreadTasks.add(task)
            if (task.needCall()) {
                task.setTaskCallBack(object : TaskCallBack {
                    override fun call() {
                        TaskStat.markTaskDone()
                        task.isFinished = true
                        satisfyChildren(task)
                        markTaskDone(task)
                        DispatcherLog.i("${task.javaClass.simpleName} finish")
                        Log.i("testLog", "call")
                    }
                })
            }
        } else {
            // 直接发，是否执行取决于具体线程池
            val future = task.runOn()?.submit(DispatchRunnable(task, this))
            future?.let {
                mFutures.add(it)
            }
        }
    }

    fun executeTask(task: Task) {
        if (ifNeedWait(task)) {
            mNeedWaitCount.getAndIncrement()
        }
        task.runOn()?.execute(DispatchRunnable(task, this))
    }

    @UiThread
    fun await() {
        try {
            if (DispatcherLog.isDebug) {
                DispatcherLog.i("still has ${mNeedWaitCount.get()}")
                for (task in mNeedWaitTasks) {
                    DispatcherLog.i("needWait: ${task.javaClass.simpleName}")
                }
            }
            if (mNeedWaitCount.get() > 0) {
                mCountDownLatch?.await(WAIT_TIME.toLong(), TimeUnit.MILLISECONDS)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }



    private fun printDependedMsg(isPrintAllTask: Boolean) {
        DispatcherLog.i("needWait size : ${mNeedWaitCount.get()}")
        if (isPrintAllTask) {
            for (cls in mDependedHashMap.keys) {
                DispatcherLog.i("cls: ${cls.simpleName} ${mDependedHashMap[cls]?.size}")
                mDependedHashMap[cls]?.let {
                    for (task in it) {
                        DispatcherLog.i("cls:${task.javaClass.simpleName}")
                    }
                }
            }
        }
    }

    companion object {
        private const val WAIT_TIME = 10000
        var context: Application? = null
            private set
        var isMainProcess = false
            private set

        @Volatile
        private var sHasInit = false

        fun init(context: Application?) {
            context?.let {
                Companion.context = it
                sHasInit = true
                isMainProcess = StaterUtils.isMainProcess(context)
            }
        }

        fun createInstance(): TaskDispatcher {
            if (!sHasInit) {
                throw RuntimeException("must call TaskDispatcher.init first")
            }
            return TaskDispatcher()
        }
    }

    fun createInstance(): TaskDispatcher {
        if (!sHasInit) {
            throw RuntimeException("must call TaskDispatcher.init first")
        }
        return TaskDispatcher()
    }
}