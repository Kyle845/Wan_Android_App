package com.example.mod_main.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.lib_common.constant.KEY_TITLE
import com.example.lib_common.constant.KEY_URL
import com.example.lib_framework.base.BaseDataBindActivity
import com.example.mod_main.databinding.ActivityArticleDetailBinding

class ArticleDetailActivity : BaseDataBindActivity<ActivityArticleDetailBinding>() {
    private var mTitle = ""

    companion object {
        fun start(context: Context, url: String, title: String) {
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.putExtra(KEY_URL, url)
            intent.putExtra(KEY_TITLE, title)
            context.startActivity(intent)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
    }
}