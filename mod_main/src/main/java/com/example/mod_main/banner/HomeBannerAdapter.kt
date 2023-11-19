package com.example.mod_main.banner

import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.example.lib_banner.base.BaseBannerAdapter
import com.example.lib_common.holder.BannerImageHolder
import com.example.lib_common.model.Banner

class HomeBannerAdapter : BaseBannerAdapter<Banner, BannerImageHolder>() {
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerImageHolder {
        val imageView = AppCompatImageView(parent.context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        return BannerImageHolder(imageView)
    }

    override fun onBindView(holder: BannerImageHolder, data: Banner, position: Int, pageSize: Int) {
        data.imagePath?.let {
//            holder.imageView.setUrl(it)
        }
    }
}