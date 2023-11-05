package com.example.lib_common.service

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

interface IMainService : IProvider {

    fun toMain(context: Context, index: Int)

    fun toArticleDetail(context: Context, url: String, title: String)
}