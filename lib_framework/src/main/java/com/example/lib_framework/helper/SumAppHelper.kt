package com.example.lib_framework.helper

import android.app.Application

object SumAppHelper {
    private lateinit var app: Application
    private var isDebug = false

    fun init(application: Application,isDebug: Boolean) {
        this.app = application
        this.isDebug = isDebug
    }

    fun getApplication() = app

    fun isDebug() = isDebug
}