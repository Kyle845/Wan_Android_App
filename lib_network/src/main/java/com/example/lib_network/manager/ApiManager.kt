package com.example.lib_network.manager

import com.example.lib_network.api.ApiInterface
import com.sum.network.manager.HttpManager

/**
 * @author mingyan.su
 * @date   2023/2/27 21:14
 * @desc   API管理器
 */
object ApiManager {
    val api by lazy { HttpManager.create(ApiInterface::class.java) }
}