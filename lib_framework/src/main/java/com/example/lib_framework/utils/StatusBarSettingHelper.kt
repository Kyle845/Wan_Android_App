package com.example.lib_framework.utils

import android.app.Activity
import android.view.View
import com.example.lib_framework.R

object StatusBarSettingHelper {
    fun statusBarLightMode(activity: Activity) {
        if (!needSetStatusBarLightMode(activity)) {
            return
        }
        statusBarLightMode(activity, true)
    }

    private fun needSetStatusBarLightMode(activity: Activity): Boolean {
        return if (activity is StatusBarLightModeInterface) {
            (activity as StatusBarLightModeInterface).needSetStatusBarLightMode()
        } else true
    }

    fun statusBarLightMode(activity: Activity, isLightModel: Boolean) {
        val navStatusBar =
            activity.findViewById<View>(R.id.nav_status_bar)
        navStatusBar?.setBackgroundColor(activity.resources.getColor(android.R.color.transparent))
        val result: Int = if (isLightModel) {
            StatusBar
        }
    }

    interface StatusBarLightModeInterface {
        fun needSetStatusBarLightMode(): Boolean
    }
}