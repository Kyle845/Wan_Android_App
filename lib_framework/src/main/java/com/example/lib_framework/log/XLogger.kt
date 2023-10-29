package com.example.lib_framework.log

import android.app.Application
import android.util.Log

class XLogger {

    fun init(context: Application,isDebug: Boolean, logPath: String, namePrefix: String = "sumTea") {
        System.loadLibrary("c++_shared")
        System.loadLibrary("marsxlog")

        val cachePath = context.filesDir.absolutePath + "sumTea/xlog"
    }

    fun v(tag: String, msg: String) {
        Log.v(tag, msg)
    }

    fun d(tag: String, msg: String) {
        Log.d(tag, msg)
    }

    fun i(tag: String, msg: String) {
        Log.i(tag, msg)
    }

    fun w(tag: String, msg: String) {
        Log.w(tag, msg)
    }

    fun e(tag: String, msg: String) {
        Log.e(tag, msg)
    }
}