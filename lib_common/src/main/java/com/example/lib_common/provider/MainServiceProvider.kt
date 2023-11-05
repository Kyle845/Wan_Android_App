package com.example.lib_common.provider

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.example.lib_common.constant.MAIN_SERVICE_HOME
import com.example.lib_common.service.IMainService

object MainServiceProvider {

    @Autowired(name = MAIN_SERVICE_HOME)
    lateinit var mainService: IMainService

    init {
        ARouter.getInstance().inject(this)
    }

    fun toMain(context: Context, index: Int = 0) {
        mainService.toMain(context, index)
    }

    fun toArticleDetail(context: Context, url: String, title: String) {
        mainService.toArticleDetail(context, url, title)
    }
}