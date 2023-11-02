package com.example.wan_android_app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.multidex.MultiDex
import com.example.lib_framework.log.LogUtil
import com.example.lib_framework.manager.AppFrontBack
import com.example.lib_framework.manager.AppFrontBackListener
import com.example.lib_framework.toast.TipsToast
import com.example.lib_stater.dispatcher.TaskDispatcher

class WanAndroidApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        appFrontBackRegister()
        registerActivityLifecycle()
        TipsToast.init(this)
        TaskDispatcher.init(this)
        val dispatcher: TaskDispatcher = TaskDispatcher.createInstance()

        dispatcher.addTask(InitSumHelperTask(this))
            .addTask(InitMmkvTask())
            .addTask(InitAppManagerTask())
            .addTask(InitRefreshLayoutTask())
            .addTask(InitArouterTask())
            .start()
        dispatcher.await()
    }

    private fun appFrontBackRegister() {
        AppFrontBack.register(this, object : AppFrontBackListener {
            override fun onBack(activity: Activity?) {
                LogUtil.d("onBack")
            }

            override fun onFront(activity: Activity?) {
                LogUtil.d("onFront")
            }
        })
    }

    private fun registerActivityLifecycle() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onActivityStarted(p0: Activity) {
                TODO("Not yet implemented")
            }

            override fun onActivityResumed(p0: Activity) {
                TODO("Not yet implemented")
            }

            override fun onActivityPaused(p0: Activity) {
                TODO("Not yet implemented")
            }

            override fun onActivityStopped(p0: Activity) {
                TODO("Not yet implemented")
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
                TODO("Not yet implemented")
            }

            override fun onActivityDestroyed(p0: Activity) {
                TODO("Not yet implemented")
            }

        })
    }
}