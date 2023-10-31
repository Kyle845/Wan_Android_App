package com.example.lib_framework.manager

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.example.lib_framework.log.LogUtil
import com.example.lib_framework.utils.DeviceInfoUtils
import java.lang.Integer.min
import kotlin.math.max

object AppManager {

    private val TAG = AppManager::class.java.simpleName

    private lateinit var mContext: Application

    private var mScreenWidthPx = 0

    private var mScreenHeightPx = 0

    private var mScreenWidthDp = 0

    private var mScreenHeightDp = 0

    /**
     * density dpi
     */
    private var mDensityDpi = 0

    /**
     * density scale
     */
    private var mDensity = 0f

    /**
     * status bar height
     */
    private var mStatusBarHeight = 0

    /**
     * product type
     */
    private var mProductType: String? = null

    private var mIsBiggerScreen = false

    fun init(application: Application) {
        mContext = application
        val windowManager =
            application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        mScreenHeightPx = max(metrics.heightPixels, metrics.widthPixels)
        mScreenWidthPx = min(metrics.heightPixels, metrics.widthPixels)
        mIsBiggerScreen = mScreenHeightPx * 1.0 / mScreenWidthPx > 16.0 / 9
        mDensityDpi = metrics.densityDpi
        mDensity = metrics.density
        mScreenHeightDp = (mScreenHeightPx / mDensity).toInt()
        mScreenWidthDp = (mScreenWidthPx / mDensity).toInt()

        val resourceId =
            application.resources.getIdentifier("status_bar_height", "dimen", "android")
        mStatusBarHeight = application.resources.getDimensionPixelSize(resourceId)
        mProductType = genProductType()
    }

    fun getScreenWidthPx(): Int {
        return mScreenWidthPx
    }

    fun getScreenHeightPx(): Int {
        return mScreenHeightPx
    }

    fun getScreenContentHeightPx(): Int {
        return mScreenHeightPx - getStatusBarHeight()
    }

    fun getScreenWidthDp(): Int {
        return mScreenWidthDp
    }

    fun getScreenHeightDp(): Int {
        return mScreenHeightDp
    }

    fun getDensityDpi(): Int {
        return mDensityDpi
    }

    fun getDensity(): Float {
        return mDensity
    }

    fun getStatusBarHeight(): Int {
        return mStatusBarHeight
    }

    fun getProductType(): String? {
        return mProductType
    }

    private fun genProductType(): String? {
        val model = DeviceInfoUtils.phoneModel
        return model.replace("[:{} \\[\\]\"']*".toRegex(),"")
    }

    fun getSmartBarHeight(): Int {
        if (isMeizu() && hasSmartBar()) {
            val autoHideSmartBar = Settings.System.getInt(
                mContext.contentResolver,
                "mz_smartbar_auto_hide",0
            ) == 1
            return if (autoHideSmartBar) {
                0
            } else {
                getNormalNavigationBarHeight()
            }
        }
        return 0
    }

    private fun getNormalNavigationBarHeight(): Int {
        try {
            val res: Resources = mContext.resources
            val rid = res.getIdentifier("config_showNavigationBar","bool","android")
            if (rid > 0) {
                val flag = res.getBoolean(rid)
                if (flag) {
                    val resourceId =
                        res.getIdentifier("navigation_bar_height","dimen","android")
                    if (resourceId > 0) {
                        return  res.getDimensionPixelSize(resourceId)
                    }
                }
            }
        } catch (e : Throwable) {
            e.printStackTrace()
        }
        return 0
    }

    fun isMeizu(): Boolean {
        return Build.MANUFACTURER.equals("Meizu", ignoreCase = true)
    }

    fun hasSmartBar(): Boolean {
        try {
            val method =
                Class.forName("android.os.Build").getMethod("hasSmartBar")
            return method.invoke(null) as Boolean
        } catch (e: Exception) {
            Log.e(TAG, "hasSmartBar",e)
        }
        if (Build.DEVICE == "mx2") {
            return true
        } else if (Build.DEVICE == "mx" || Build.DEVICE == "m9") {
            return false
        }
        return false
    }

    fun isBiggerScreen(): Boolean {
        return mIsBiggerScreen
    }

    fun getDeviceBuildBrand(): String {
        return Build.BRAND ?: ""
    }

    fun getDeviceBuildModel(): String {
        return DeviceInfoUtils.phoneModel
    }

    fun getDeviceBuildRelease(): String {
        return Build.VERSION.RELEASE ?: ""
    }

    fun dip2px(dipValue: Float): Int {
        return (dipValue * mDensity + 0.5f).toInt()
    }

    fun getAppVersionName(context: Context): String {
        var versionName = ""
        try {
            val pm = context.packageManager
            val packageName = context.packageName ?: "com.example.wan_android_app"
            pm.getPackageInfo(packageName, 0).versionName
            val pi = pm.getPackageInfo(packageName, 0)
            versionName = pi.versionName
            if (versionName.isNullOrEmpty()) {
                return ""
            }
        } catch (e: Exception) {
            LogUtil.e("VersionInfo", e)
        }
        return versionName
    }

    fun getAppVersionCode(context: Context): Long {
        var appVersionCode: Long = 0
        try {
            val packageName = context.packageName ?: "com.example.wan_android_app"
            val packageInfo = context.applicationContext
                .packageManager
                .getPackageInfo(packageName, 0)
            appVersionCode = packageInfo.versionCode.toLong()
        } catch (e: PackageManager.NameNotFoundException) {
            LogUtil.e("getAppVersionCode-${e.message}")
        }
        return appVersionCode
    }

    override fun toString(): String {
        return ("PhoneInfoManager{"
                + "mScreenWidthPx="
                + mScreenWidthPx
                + ", mScreenHeightPx="
                + mScreenHeightPx
                + ", mScreenWidthDp="
                + mScreenWidthDp
                + ", mScreenHeightDp="
                + mScreenHeightDp
                + ", mDensityDpi="
                + mDensityDpi
                + ", mDensity="
                + mDensity
                + ", mStatusBarHeight="
                + mStatusBarHeight
                + '}')
    }
}