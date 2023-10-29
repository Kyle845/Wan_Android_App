package com.example.lib_stater.task
import android.os.Process
import androidx.annotation.IntRange
import java.util.concurrent.Executor
interface ITask {
    @IntRange(from = Process.THREAD_PRIORITY_FOREGROUND.toLong(),to = Process.THREAD_PRIORITY_LOWEST.toLong())
    fun priority(): Int

    fun run()

    fun runOn(): Executor?

    fun dependsOn(): List<Class<out Task?>?>?

    fun needWait(): Boolean

    fun runOnMainThread(): Boolean

    fun onlyInMainProcess(): Boolean

    val tailRunnable: Runnable?

    fun setTaskCallBack(callBack: TaskCallBack?)

    fun needCall(): Boolean
}

interface TaskCallBack {
    fun call()
}