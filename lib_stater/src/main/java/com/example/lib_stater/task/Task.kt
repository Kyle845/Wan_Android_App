package com.example.lib_stater.task

import android.content.Context
import com.example.lib_stater.dispatcher.TaskDispatcher
import com.example.lib_stater.dispatcher.TaskDispatcher.Companion.context
import java.util.concurrent.CountDownLatch
import android.os.Process
import com.example.lib_stater.utils.DispatcherExecutor
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

abstract class Task: ITask {
    protected var mContext: Context? = context

    protected var mIsMainProcess: Boolean = TaskDispatcher.isMainProcess

    @Volatile
    var isWaiting = false

    @Volatile
    var isRunning = false

    @Volatile
    var isFinished = false

    @Volatile
    var isSend = false

    private val mDepends = CountDownLatch(
        dependsOn()?.size ?: 0
    )

    fun waitToSatisfy() {
        try {
            mDepends.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun satisfy() {
        mDepends.countDown()
    }

    fun needRunAsSoon(): Boolean {
        return false
    }

    override fun priority(): Int {
        return Process.THREAD_PRIORITY_BACKGROUND
    }

    override fun runOn(): ExecutorService? {
        return DispatcherExecutor.iOExecutor
    }

    override fun needWait(): Boolean {
        return false
    }

    override fun dependsOn(): List<Class<out Task?>?>? {
        return null
    }

    override fun runOnMainThread(): Boolean {
        return false
    }

    override val tailRunnable: Runnable?
        get() = null

    override fun setTaskCallBack(callBack: TaskCallBack?) {

    }

    override fun onlyInMainProcess(): Boolean {
        return true
    }

    abstract class MainTask : Task() {
        override fun runOnMainThread(): Boolean {
            return true
        }
    }
}