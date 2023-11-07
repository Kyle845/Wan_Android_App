package com.example.lib_framework.base

import android.util.Log
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import com.example.lib_framework.ext.saveAs
import com.example.lib_framework.ext.saveAsUnChecked
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType

abstract class BaseDataBindActivity<DB : ViewBinding> : BaseActivity() {
    lateinit var mBinding: DB

    override fun setContentLayout() {
        val type = javaClass.genericSuperclass
        val vbClass: Class<DB> = type!!.saveAs<ParameterizedType>().actualTypeArguments[0].saveAs()
        val method = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        try {
            mBinding = method.invoke(this, layoutInflater)!!.saveAsUnChecked()
            setContentView(mBinding.root)
        } catch (e : InvocationTargetException) {
            Log.e("Oops!",e.cause.toString())
        }
    }

    override fun getLayoutResId(): Int = 0
}