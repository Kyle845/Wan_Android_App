package com.example.lib_framework.utils

import android.app.Activity
import android.os.Looper
import android.widget.Toast
import androidx.annotation.CallSuper
import com.example.lib_framework.R
import com.example.lib_framework.log.LogUtil
import java.util.logging.Handler
import kotlin.system.exitProcess

object AppExit {

    private var preExit = false

    private val handler = android.os.Handler(Looper.getMainLooper()) {
        preExit = false
        true
    }

    @CallSuper
    fun onBackPressed(
        activity: Activity,
        tipCallback: () -> Unit = {
            Toast.makeText(
                activity,
                activity.getString(R.string.base_app_exit_one_more_press),
                Toast.LENGTH_SHORT
            ).show()
        },
        exitCallback: () -> Unit = {}
    ) {
        if (!preExit) {
            preExit = true
            tipCallback()
            handler.sendEmptyMessageDelayed(0,2000)
        } else {
            exitCallback()
            LogUtil.flushLog()
            activity.finish()
            exitProcess(0)
        }
    }
}