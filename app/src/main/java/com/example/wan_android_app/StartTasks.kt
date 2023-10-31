package com.example.wan_android_app

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.example.lib_framework.helper.SumAppHelper
import com.example.lib_framework.log.LogUtil
import com.example.lib_framework.manager.AppManager
import com.example.lib_stater.task.Task
import com.example.lib_stater.utils.DispatcherExecutor
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.mmkv.BuildConfig
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel
import java.util.concurrent.ExecutorService

class InitSumHelperTask(val application: Application) : Task() {
    override fun needWait(): Boolean {
        return true
    }

    override fun run() {
        SumAppHelper.init(application, BuildConfig.DEBUG)
    }
}

class InitMmkvTask() : Task() {

    override fun run() {
        val rootDir: String = MMKV.initialize(SumAppHelper.getApplication())
        MMKV.setLogLevel(
            if (BuildConfig.DEBUG) {
                MMKVLogLevel.LevelDebug
            } else {
                MMKVLogLevel.LevelError
            }
        )
        LogUtil.d("mmkv root: $rootDir", tag = "MMKV")
    }

    override fun needWait(): Boolean {
        return true
    }

    override fun dependsOn(): MutableList<Class<out Task>> {
        val tasks = mutableListOf<Class<out Task?>>()
        tasks.add(InitSumHelperTask::class.java)
        return tasks
    }

    override fun runOn(): ExecutorService? {
        return DispatcherExecutor.iOExecutor
    }
}

class InitAppManagerTask() : Task() {
    override fun run() {
        AppManager.init(SumAppHelper.getApplication())
    }

    override fun needWait(): Boolean {
        return true
    }

    override fun dependsOn(): MutableList<Class<out Task>> {
        val tasks = mutableListOf<Class<out Task>>()
        tasks.add(InitSumHelperTask::class.java)
        return tasks
    }
}

class InitRefreshLayoutTask(): Task() {

    override fun needWait(): Boolean {
        return true
    }

    override fun run() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator {context, layout ->
            layout.setPrimaryColorsId(android.R.color.white)
            ClassicsHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator {context, layout ->
            ClassicsFooter(context)
        }
    }
}

class InitArouterTask() : Task() {

    override fun needWait(): Boolean {
        return true
    }

    override fun dependsOn(): MutableList<Class<out Task>> {
        val tasks = mutableListOf<Class<out Task?>>()
        tasks.add(InitSumHelperTask::class.java)
        return tasks
    }

    override fun run() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(SumAppHelper.getApplication())
    }

}