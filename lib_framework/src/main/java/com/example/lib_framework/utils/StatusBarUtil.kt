package com.example.lib_framework.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import com.example.lib_framework.log.LogUtil

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
            if (setStatusBarModeForAndroidM(activity.window,true)) {
                result = STATUS_BAR_TYPE_ANDROID_M
            }
            if (setStatusBarModeForMIUI(activity.window,true)) {
                result = STATUS_BAR_TYPE_MI_UI
            }
            if (setStatusBarModeForFlyMe(activity.window, true)) {
                result = STATUS_BAR_TYPE_FLYME
            }
        }
        return result
    }

    fun setStatusBarDarkMode(activity: Activity?): Int {
        var result = STATUS_BAR_TYPE_DEFAULT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && activity != null) {
            if (setStatusBarModeForAndroidM(activity.window,false)) {
                result = STATUS_BAR_TYPE_ANDROID_M
            }
            if (setStatusBarModeForMIUI(activity.window,false)) {
                result = STATUS_BAR_TYPE_MI_UI
            }
            if (setStatusBarModeForFlyMe(activity.window, false)) {
                result = STATUS_BAR_TYPE_FLYME
            }
        }
        return result
    }

    private fun setStatusBarModeForMIUI(
        window: Window?,
        darkText: Boolean
    ) : Boolean {
        var result = false
        if (window != null) {
            val clazz: Class<*> = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams =
                    Class.forName("android.view.MiuiWIndowManager\$LayoutParams")
                val field =
                    layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod(
                    "setExtraFlags",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                if (darkText) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)
                } else {
                    extraFlagField.invoke(window,0,darkModeFlag)
                }
                var vis = window.decorView.systemUiVisibility
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                vis =
                    if (darkText) {
                        vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    } else {
                        vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                window.decorView.systemUiVisibility = vis
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.statusBarColor = Color.TRANSPARENT
                }
                result = true
            } catch (e : Exception) {
                LogUtil.w(e)
            }
        }
        return result
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

    private fun setStatusBarModeForFlyMe(
        window: Window?,
        darkText: Boolean
    ): Boolean {
        var result = false
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                    .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                value = if (darkText) {
                    value or bit
                } else {
                    value or bit.inv()
                }
                meizuFlags.setInt(lp,value)
                window.attributes = lp
                result = true
            } catch (e: Exception) {
                LogUtil.w(e)
            }
        }
        return result
    }

    fun setStatusBarModeForOpp(
        window: Window,
        darkText: Boolean
    ): Boolean {
        var result = false
        try {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            var vis = window.decorView.systemUiVisibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                vis = if (darkText) {
                    vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    vis = if (darkText) {
                        vis or SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT
                    } else {
                        vis and SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT.inv()
                    }
                }
                window.decorView.systemUiVisibility = vis
                result = true
            } catch (e: Exception) {
                LogUtil.w(e)
            }
            return result
        }

    fun setColor(
        activity: Activity, @ColorInt color: Int,
        statusBarAlpha: Int
    ) {
        setFullScreen(activity)
    }

    fun setFullScreen(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    class StatusBarView : View {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, attrs: AttributeSet?) : super(
            context,
            attrs)
        constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int
        ) : super(context, attrs, defStyleAttr)
    }

    fun setStatusBar (
        activity: Activity,
        darkContent: Boolean,
        statusBarColor: Int = Color.WHITE,
        translucent: Boolean
    ) {
        val window = activity.window

        val decorView = window.decorView

        var visibility = decorView.systemUiVisibility

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = statusBarColor
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            visibility = if (darkContent) {
                visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }

        if (translucent) {
            visibility = visibility or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        decorView.systemUiVisibility = visibility
    }


}