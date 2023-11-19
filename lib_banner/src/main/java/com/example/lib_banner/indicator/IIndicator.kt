package com.example.lib_banner.indicator

import androidx.viewpager.widget.ViewPager
import com.example.lib_banner.options.IndicatorOptions

/**
 * IIndicator
 */
interface IIndicator : ViewPager.OnPageChangeListener {

    fun notifyDataChanged()

    fun setIndicatorOptions(options: IndicatorOptions)
}
