package com.example.lib_framework.base

import androidx.appcompat.app.AppCompatActivity
import com.example.lib_framework.utils.LoadingUtils

abstract class BaseActivity : AppCompatActivity() {
    protected var TAG: String? = this::class.java.simpleName

    private val dialogUtils by lazy {
        LoadingUtils(this)
    }
}