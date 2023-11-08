package com.example.lib_banner

import android.content.Context
import android.graphics.Path
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.lifecycle.LifecycleObserver
import androidx.viewpager2.widget.ViewPager2

open class BannerViewPager<T, H : BaseViewHolder<T>> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RelativeLayout(context, attrs), LifecycleObserver {
    private var currentPosition = 0

    private var isCustomIndicator = false

    private var isLooping = false

    private var mOnPageClickListener: OnPageClickListener? = null

    private var mIndicatorView: IIndicator? = null

    private var mIndicatorLayout: RelativeLayout? = null

    private var mViewPager: ViewPager2? = null

    private var mBannerManager: BannerManager = BannerManager()

    private val mHandler = Handler(Looper.getMainLooper())

    private var mBannerPagerAdapter: BaseBannerAdapter<T, H>? = null

    private var onPageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    private val mRunnable = Runnable { handlePosition() }

    private var mRadiusRectF: RectF? = null
    private var mRadiusPath: Path? = null

    private var startX = 0
    private var startY = 0
}