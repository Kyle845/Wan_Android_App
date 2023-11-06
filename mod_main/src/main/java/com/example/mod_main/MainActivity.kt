package com.example.mod_main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.lib_common.constant.KEY_INDEX
import com.example.lib_common.constant.MAIN_ACTIVITY_HOME
import com.example.lib_framework.base.BaseDataBindActivity
import com.example.lib_framework.log.LogUtil
import com.example.lib_framework.toast.TipsToast
import com.example.lib_framework.utils.AppExit
import com.example.lib_framework.utils.StatusBarSettingHelper
import com.example.mod_main.databinding.ActivityMainBinding
import com.example.mod_main.navigator.SumFragmentNavigator

@Route(path = MAIN_ACTIVITY_HOME)
class MainActivity : BaseDataBindActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController

    companion object {
        fun start(context: Context, index: Int = 0) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(KEY_INDEX, index)
            context.startActivity(intent)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        val navView = mBinding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val fragmentNavigator =
            SumFragmentNavigator(this, navHostFragment.childFragmentManager, navHostFragment.id)
        navController.navigatorProvider.addNavigator(fragmentNavigator)
        navController.setGraph(R.navigation.mobile_navigation)
        navView.setupWithNavController(navController)
        StatusBarSettingHelper.setStatusBarTranslucent(this)
        StatusBarSettingHelper.statusBarLightMode(this@MainActivity, true)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            val index = intent.getIntExtra(KEY_INDEX, 0)
            LogUtil.e("onNewIntent:index:$index", tag = "smy")
        }
    }

    override fun onBackPressed() {
        AppExit.onBackPressed(this,
            { TipsToast.showTips(getString(R.string.app_exit_one_more_press)) })

    }
}