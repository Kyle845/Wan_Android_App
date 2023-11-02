package com.example.lib_framework.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.example.lib_framework.R
import com.example.lib_framework.toast.TipsToast
import com.example.lib_framework.utils.LoadingUtils

abstract class BaseFragment : Fragment() {
    protected var TAG: String? = this::class.java.simpleName

    protected var mIsViewCreate = false

    private val dialogUtils by lazy {
        LoadingUtils(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        return getContentView(inflater,container)
    }

    open fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(getLayoutResId(), null)
    }

    abstract fun getLayoutResId(): Int

    abstract fun initView(view: View,saveInstanceState: Bundle?)

    open fun initData() {}

    fun showLoading() {
        showLoading(getString(R.string.default_loading))
    }

    fun showLoading(msg: String?) {
        dialogUtils.showLoading(msg)
    }

    fun showLoading(@StringRes res: Int) {
        showLoading(getString(res))
    }

    fun dismissLoading() {
        dialogUtils.dismissLoading()
    }

    fun showToast(@StringRes resId: Int) {
        TipsToast.showTips(resId)
    }
}