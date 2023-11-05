package com.example.mod_main.ui.system

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.lib_common.provider.MainServiceProvider
import com.example.lib_framework.base.BaseDataBindActivity
import com.example.lib_framework.ext.countDownCoroutines
import com.example.lib_framework.ext.onClick
import com.example.lib_framework.utils.StatusBarSettingHelper
import com.example.mod_main.R
import com.example.mod_main.databinding.ActivitySplashBinding

class SplashActivity : BaseDataBindActivity<ActivitySplashBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        StatusBarSettingHelper.setStatusBarTranslucent(this)
        mBinding.tvSkip.onClick {
            MainServiceProvider.toMain(this)
        }
        countDownCoroutines(2, lifecycleScope, onTick = {
            mBinding.tvSkip.text = getString(R.string.splash_time,it.plus(1).toString())
        }) {
            MainServiceProvider.toMain(this)
            finish()
        }
    }
}