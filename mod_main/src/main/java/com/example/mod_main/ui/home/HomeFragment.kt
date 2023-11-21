package com.example.mod_main.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.util.SparseArray
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lib_common.model.ProjectTabItem
import com.example.lib_common.provider.SearchServiceProvider
import com.example.lib_framework.base.BaseMvvmFragment
import com.example.lib_framework.ext.gone
import com.example.lib_framework.ext.onClick
import com.example.lib_framework.ext.visible
import com.example.lib_framework.utils.getStringFromResource
import com.example.mod_main.R
import com.example.mod_main.databinding.FragmentHomeBinding
import com.example.mod_main.ui.home.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class HomeFragment : BaseMvvmFragment<FragmentHomeBinding, HomeViewModel>(), OnRefreshListener {
    private val mArrayTabFragments = SparseArray<Fragment>()
    private var mTabLayoutMediator: TabLayoutMediator? = null
    private var mProjectTabs: MutableList<ProjectTabItem> = mutableListOf()

    override fun initView(view: View, saveInstanceState: Bundle?) {
        mBinding?.refreshLayout?.apply {
            autoRefresh()
            setEnableRefresh(true)
            setEnableLoadMore(false)
            setOnRefreshListener(this@HomeFragment)
        }
        mBinding?.ivSearch?.onClick {
            SearchServiceProvider.toSearch(requireContext())
        }
        initTab()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refresh()
    }

    private fun refresh() {
        mViewModel.getBannerList().observe(this) { banners ->
            banners?.let {
                mBinding?.bannerHome?.visible()
                mBinding?.bannerHome?.setData(it)
            } ?: kotlin.run {
                mBinding?.bannerHome?.gone()
            }
            mBinding?.refreshLayout?.finishRefresh()
        }
        mViewModel.getProjectTab().observe(this) {tabs ->
            mProjectTabs =
                mProjectTabs.filter { it.name == getStringFromResource(R.string.home_tab_video_title) }.toMutableList()
            tabs?.forEachIndexed { index, item ->
                mProjectTabs.add(item)
                mArrayTabFragments.append(index + 1, HomeTabFragment.newInstance(tabs[index].id))
            }
            mFragmentApapter?.setData(mArrayTabFragments)
            mFragmentApapter?.notifyItemRangeChanged(1, mArrayTabFragments.size())
            mBinding?.tabHome?.let {
                it.post {it.getTabAt(0)?.select()}
            }
        }
    }

    private fun initTab() {
        mArrayTabFragments.append(0, HomeVideoFragment())
        mProjectTabs.add(0, ProjectTabItem(id = 0, getStringFromResource(R.string.home_tab_video_title)))
        activity?.let {
            mFragmentAdapter = ViewPager2FragmentAdapter(childFragmentManager, lifecycle, mArrayTabFragments)
        }
        mBinding?.let {
            it.viewPager.adapter = mFragmentAdapter
            it.viewPager.isUserInputEnabled = true
            it.viewPager.offscreenPageLimit= mArrayTabFragments.size()
            mTabLayoutMediator = TabLayoutMediator(it.tabHome, it.viewPager) { tab: TabLayout.Tab, position: Int ->
                tab.text = mProjectTabs[position].name
            }
            mTabLayoutMediator?.attach()
            it.tabHome.addOnTabSelectedListener(tabSelectedCall)
            val tabFirst = it.tabHome.getTabAt(0)
            setTabTextSize(tabFirst)
        }
    }
    private val tabSelectedCall = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            setTabTextSize(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            tab?.customView = null
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }

    private fun setTabTextSize(tabFirst: TabLayout.Tab?) {
        TextView(requireContext()).apply {
            typeface = Typeface.DEFAULT_BOLD
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }.also {
            it.text = tabFirst?.text
            tabFirst?.customView = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mTabLayoutMediator?.detach()
    }
}