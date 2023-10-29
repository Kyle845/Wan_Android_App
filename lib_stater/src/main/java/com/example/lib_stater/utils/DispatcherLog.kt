package com.example.lib_stater.utils

import com.example.lib_framework.helper.SumAppHelper
import com.example.lib_framework.log.LogUtil

object DispatcherLog {
    var isDebug = SumAppHelper.isDebug()

    @JvmStatic
    fun i(msg: String?) {
        if (msg == null) {
            return
        }
        LogUtil.i(msg, tag = "StartTask")
    }
}