package com.example.lib_framework.base

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.example.lib_framework.R
import com.example.lib_framework.toast.TipToast
import com.example.lib_framework.utils.LoadingUtils

abstract class BaseActivity : AppCompatActivity() {
    protected var TAG: String? = this::class.java.simpleName

    private val dialogUtils by lazy {
        LoadingUtils(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout()
        initView(savedInstanceState)
        initData()
    }

    open fun setContentLayout() {
        setContentView(getLayoutResId())
    }

    abstract fun getLayoutResId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    open fun initData() {}

    fun showLoading() {
        showLoading(getString(R.string.default_loading))
    }

    fun showLoading(msg: String?) {
        dialogUtils.showLoading(msg)
    }

    fun dismissLoading() {
        dialogUtils.dismissLoading()
    }

    fun showToast(@StringRes resId: Int) {
        TipToast.showTips(resId)
    }
}