package com.example.lib_framework.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle

object AppFrontBack {
    private var activityStartCount = 0

    fun register(application: Application, listener: AppFrontBackListener) {
        application.registerActivityLifecycleCallbacks(object: Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity){}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
                activityStartCount++
                if (activityStartCount == 1) {
                    listener.onFront(activity)
                }
            }

            override fun onActivityStopped(activity: Activity) {
               activityStartCount--;
                if (activityStartCount == 0) {
                    listener.onBack(activity)
                }
            }
        })
    }
}

interface AppFrontBackListener {
    fun onFront(activity: Activity?)

    fun onBack(activity: Activity?)
}