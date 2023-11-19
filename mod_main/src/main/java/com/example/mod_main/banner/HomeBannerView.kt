package com.example.mod_main.banner

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.example.lib_banner.BannerViewPager
import com.example.lib_common.holder.BannerImageHolder
import com.example.lib_common.model.Banner
import com.example.lib_common.provider.MainServiceProvider
import com.example.lib_framework.utils.dpToPx

class HomeBannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BannerViewPager<Banner, BannerImageHolder>(context, attrs)  {
    private val mAdapter = HomeBannerAdapter()

    init {
        setAdapter(mAdapter)
            .setAutoPlay(true)
            .setScrollDuration(500)
            .setCanLoop(true)
            .setInterval(2000L)
            .setIndicatorSliderWidth(dpToPx(6))
            .setIndicatorSliderColor(Color.parseColor("#8F8E94"), Color.parseColor("#0165b8"))
            .create()
        mAdapter.setPageClickListener(object : OnPageClickListener {
            override fun onPageClick(clickedView: View?, position: Int) {
                val item = mAdapter.getData()[position]
                if (!item.url.isNullOrEmpty()) {
                    MainServiceProvider.toArticleDetail(
                        context = context,
                        url = item.url!!,
                        title = item.title ?: ""
                    )
                }
            }
        })
    }

    fun setData(list: MutableList<Banner>) {
        refreshData(list)
    }
}