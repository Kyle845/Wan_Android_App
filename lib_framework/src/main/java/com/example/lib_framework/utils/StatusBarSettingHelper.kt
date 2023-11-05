package com.example.lib_framework.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.graphics.ColorUtils
import com.example.lib_framework.R
import com.example.lib_framework.utils.OSUtils.isEMUI3_x

object StatusBarSettingHelper {
    fun statusBarLightMode(activity: Activity) {
        if (!needSetStatusBarLightMode(activity)) {
            return
        }
        statusBarLightMode(activity, true)
    }

    fun statusBarLightMode(activity: Activity, isLightModel: Boolean) {
        val navStatusBar =
            activity.findViewById<View>(R.id.nav_status_bar)
        navStatusBar?.setBackgroundColor(activity.resources.getColor(android.R.color.transparent))
        val result: Int = if (isLightModel) {
            StatusBarUtil.setStatusBarLightMode(activity)
        } else {
            StatusBarUtil.setStatusBarDarkMode(activity)
        }
        if (result == StatusBarUtil.STATUS_BAR_TYPE_DEFAULT) {
            if (isLightModel && navStatusBar != null) {
                navStatusBar.setBackgroundColor(activity.resources.getColor(R.color.status_bar_bg))
            }
            setupStatusBarView(activity, isLightModel)
        }
    }

    /**
     * 代码实现android:fitsSystemWindows
     *
     * @param activity
     */
    fun setRootViewFitsSystemWindows(
        activity: Activity,
        fitSystemWindows: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val winContent =
                activity.findViewById<View>(android.R.id.content) as ViewGroup
            if (winContent.childCount > 0) {
                val rootView = winContent.getChildAt(0) as ViewGroup
                if (rootView != null) {
                    rootView.fitsSystemWindows = fitSystemWindows
                }
            }
        }
    }

    private fun setupStatusBarView(mActivity: Activity, isLightMode: Boolean) {
        val window = mActivity.window
        val mDecorView = window.decorView as ViewGroup
        var statusBarView =
            mDecorView.findViewById<View>(R.id.immersion_status_bar_view)
        if (statusBarView == null) {
            statusBarView = View(mActivity)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(mActivity)
            )
            params.gravity = Gravity.TOP
            statusBarView.layoutParams = params
            statusBarView.visibility = View.VISIBLE
            statusBarView.id = R.id.immersion_status_bar_view
            mDecorView.addView(statusBarView)
        }
        if (isLightMode) {
            statusBarView.setBackgroundColor(
                ColorUtils.blendARGB(
                    Color.TRANSPARENT,
                    Color.BLACK, 0.2f
                )
            )
        } else {
            statusBarView.setBackgroundColor(
                ColorUtils.blendARGB(
                    Color.TRANSPARENT,
                    Color.TRANSPARENT, 0.0f
                )
            ) //此处默认设置0.2f的透明度，防止在4.4的部分机器上无法设置状态栏字体颜色的问题
        }
    }

    @TargetApi(19)
    fun setStatusBarTranslucent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isEMUI3_x) {
                val window = activity.window
                val decorView = window.decorView
                val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                decorView.systemUiVisibility = option
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            } else {
                initBarBelowLOLLIPOP(activity)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            initBarBelowLOLLIPOP(activity)
        }
    }

    private fun initBarBelowLOLLIPOP(activity: Activity) { //透明状态栏
        val window = activity.window
        val attributes = window.attributes
        val flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        attributes.flags = attributes.flags or flagTranslucentStatus
        //int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        //attributes.flags |= flagTranslucentNavigation;
        window.attributes = attributes
        //创建一个假的状态栏
        setupStatusBarView(activity, false)
    }

    /* */
    /**
     * 适配刘海屏
     * Fits notch screen.
     */
/*
    private void fitsNotchScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !mInitialized) {
            WindowManager.LayoutParams lp = mWindow.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            mWindow.setAttributes(lp);
        }
    }*/

    /**
     * 是否设置 状态栏的深色模式（因为白底再有些手机上状态栏的字也是白色所以看不见）
     */
    private fun needSetStatusBarLightMode(activity: Activity): Boolean {
        return if (activity is StatusBarLightModeInterface) {
            (activity as StatusBarLightModeInterface).needSetStatusBarLightMode()
        } else true
    }



    private fun getStatusBarHeight(context: Context): Int {
        val result = 0
        try {
            val resourceId = Resources.getSystem()
                .getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                val sizeOne = context.resources.getDimensionPixelSize(resourceId)
                val sizeTwo =
                    Resources.getSystem().getDimensionPixelSize(resourceId)
                return if (sizeTwo >= sizeOne) {
                    sizeTwo
                } else {
                    val densityOne =
                        context.resources.displayMetrics.density
                    val densityTwo =
                        Resources.getSystem().displayMetrics.density
                    val f = sizeOne * densityTwo / densityOne
                    (if (f >= 0) f + 0.5f else f - 0.5f).toInt()
                }
            }
        } catch (ignored: Resources.NotFoundException) {
            return 0
        }
        return result
    }

    interface StatusBarLightModeInterface {
        fun needSetStatusBarLightMode(): Boolean
    }
}