package com.example.mod_main.ui.home.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.lib_common.model.Banner
import com.example.lib_common.model.ProjectSubInfo
import com.example.lib_network.viewmodel.BaseViewModel

class HomeViewModel : BaseViewModel() {
    val projectItemLiveData = MutableLiveData<MutableList<ProjectSubInfo>?>()
    val bannersLiveData = MutableLiveData<MutableList<Banner>?>()
    val homeRepository by lazy { HomeRepository() }

}