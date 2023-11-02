package com.example.lib_framework.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window

object StatusBarUtil {
    const val STATUS_BAR_TYPE_DEFAULT = 0
    const val STATUS_BAR_TYPE_MI_UI = 1
    const val STATUS_BAR_TYPE_FLYME = 2
    const val STATUS_BAR_TYPE_ANDROID_M = 3
    const val STATUS_BAR_TYPE_OPP = 4
    const val SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010

    fun setStatusBarLightMode(activity: Activity?): Int {
        var result = STATUS_BAR_TYPE_DEFAULT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && activity != null) {
            if (setStatusBarModeForAndroidM(activity.window,false)) {
                result = STATUS_BAR_TYPE_ANDROID_M
            }
            if (setSt)
        }
    }

    private fun setStatusBarModeForMIUI(
        window: Window?,
        darkText: Boolean
    ) : Boolean {
        val result = false
        if (window != null) {
            val clazz: Class<*> = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams =
                    Class.forName("android.view.MiuiWIndowManager\$LayoutParams")
                val field =
                    layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            }
        }
    }

    private fun setStatusBarModeForAndroidM(
        window: Window?,
        darkText: Boolean
    ): Boolean {
        var result = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && window != null) {
            window.decorView.systemUiVisibility =
                if (darkText) View.SYSTEM_UI_FLAG_FULLSCREEN or 0x00002000 else View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_VISIBLE
            result = true
        }
        return result
    }
}