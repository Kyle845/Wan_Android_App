package com.example.mod_main.ui.home

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.lib_framework.base.BaseMvvmFragment
import com.example.mod_main.databinding.FragmentHomeVideoBinding
import com.example.mod_main.ui.home.viewmodel.HomeViewModel
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener

class HomeTabFragment : BaseMvvmFragment<FragmentHomeVideoBinding, HomeViewModel>(), OnRefreshListener,
    OnLoadMoreListener{
        private var mPage = 1
        private var mId: Int? = null
        private lateinit var mAdapter: Hom
}