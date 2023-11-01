package com.example.lib_framework.base

import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseDataBindActivity<DB: ViewBinding> : BaseActivity() {
    lateinit var mBinding: DB

    override fun setContentLayout() {
        val type = javaClass.genericSuperclass

    }
}