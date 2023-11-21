package com.example.mod_main.ui.home.viewmodel

import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.lib_common.constant.FILE_VIDEO_LIST
import com.example.lib_common.model.ArticleList
import com.example.lib_common.model.Banner
import com.example.lib_common.model.ProjectSubInfo
import com.example.lib_common.model.ProjectTabItem
import com.example.lib_framework.toast.TipsToast
import com.example.lib_network.flow.requestFlow
import com.example.lib_network.manager.ApiManager
import com.example.lib_network.viewmodel.BaseViewModel
import com.example.mod_main.repository.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {
    val projectItemLiveData = MutableLiveData<MutableList<ProjectSubInfo>?>()
    val bannersLiveData = MutableLiveData<MutableList<Banner>?>()
    val homeRepository by lazy { HomeRepository() }

    fun getBannerList(): LiveData<MutableList<Banner>?> {
        viewModelScope.launch {
            val data = requestFlow(requestCall = {
                ApiManager.api.getHomeBanner()
            }, errorBlock = { code, error ->
                TipsToast.showTips(error)
                bannersLiveData.value = null
            })
            bannersLiveData.value = data
        }
        return bannersLiveData
    }

    fun getHomeInfoList(page: Int): LiveData<ArticleList> {
        return liveData {
            val response = safeApiCall(errorBlock = { code, errorMsg ->
                TipsToast.showTips(errorMsg)
            }) {
                homeRepository.getHomeInfoList(page)
            }
            response?.let {
                emit(it)
            }
        }
    }

    fun getProjectTab(): LiveData<MutableList<ProjectTabItem>?> {
        return liveData {
            val response = safeApiCall(errorBlock = { code, errorMsg ->
                TipsToast.showTips(errorMsg)
            }) {
                homeRepository.getProjectTab()
            }
            emit(response)
        }
    }

    fun getProjectList(page: Int, cid: Int): LiveData<MutableList<ProjectSubInfo>?> {
        launchUI(errorBlock = { code, errorMsg ->
            TipsToast.showTips(errorMsg)
            projectItemLiveData.value = null
        }) {
            val data = homeRepository.getProjectList(page, cid)
            projectItemLiveData.value = data?.datas
        }
        return projectItemLiveData
    }

//    fun getVideoList(assetManager: AssetManager): LiveData<MutableList<VideoInfo>?> {
//        return liveData {
//            val response = safeApiCall(errorBlock = { code, errorMsg ->
//                TipsToast.showTips(errorMsg)
//            }) {
//                var list = homeRepository.getVideoListCache()
//                //缓存为空则创建视频数据
//                if (list.isNullOrEmpty()) {
//                    list = ParseFileUtils.parseAssetsFile(assetManager, FILE_VIDEO_LIST)
//                    VideoCacheManager.saveVideoList(list)
//                }
//                list
//            }
//
//            emit(response)
//        }
//    }


}