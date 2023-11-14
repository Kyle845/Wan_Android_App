package com.example.lib_banner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.lib_framework.utils.ViewUtils
import kotlin.Int.Companion.MAX_VALUE
import kotlin.math.abs

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

    private val mOnPageChangeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            pageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (getGlobalVisibleRect(Rect()) && windowVisibility == View.VISIBLE) {
                pageSelected(position)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            pageScrollStateChanged(state)
        }
    }

    init {
        mBannerManager.initAttrs(context, attrs)
        initView()
    }

    private  fun initView() {
        mViewPager = ViewPager2(context).apply {
            getChildAt(0).apply {
                clipToPadding = parentClipToPadding()
                clipChildren = parentClipToPadding()
            }
        }
        addView(
            mViewPager,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mIndicatorLayout = RelativeLayout(context)
        addView(
            mIndicatorLayout,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(ALIGN_PARENT_BOTTOM)
            })
        mViewPager?.setPageTransformer(mBannerManager.getCompositePageTransformer())
        clipChildren = parentClipToPadding()
        clipToPadding = parentClipToPadding()
    }

    override fun onDetachedFromWindow() {
        if (mBannerManager.getBannerOptions().isStopLoopWhenDetachedFromWindow()) {
            stopLoop()
        }
        super.onDetachedFromWindow()
        if (context is AppCompatActivity) {
            (context as AppCompatActivity).lifecycle.removeObserver(this)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mBannerManager.getBannerOptions().isStopLoopWhenDetachedFromWindow()) {
            startLoop()
        }
        if (context is AppCompatActivity) {
            (context as AppCompatActivity).lifecycle.addObserver(this)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                isLooping = true
                stopLoop()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                isLooping = false
                startLoop()
            }
            else -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val doNotNeedIntercept =
            ((mViewPager?.isUserInputEnabled != true || (mBannerPagerAdapter != null && (mBannerPagerAdapter?.getData()?.size
                ?: 0) <= 1)))
        if (doNotNeedIntercept) {
            return super.onInterceptTouchEvent(ev)
        }
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x.toInt()
                startY = ev.y.toInt()
                parent.requestDisallowInterceptTouchEvent(
                    !mBannerManager
                        .getBannerOptions().isDisallowParentInterceptDownEvent()
                )
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x.toInt()
                val endY = ev.y.toInt()
                val disX = abs(endX - startX)
                val disY: Int = abs(endY - startY)
                val orientation: Int = mBannerManager.getBannerOptions().getOrientation()
                    ?: ViewPager2.ORIENTATION_HORIZONTAL
                if (orientation == ViewPager2.ORIENTATION_VERTICAL) {
                    onVerticalActionMove(endY, disX, disY)
                } else if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                    onHorizontalActionMove(endX, disX, disY)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                false
            )
            MotionEvent.ACTION_OUTSIDE -> {
            }
            else -> {
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun onVerticalActionMove(endY: Int, disX: Int, disY: Int) {
        if (disY > disX) {
            val canLoop: Boolean = mBannerManager.getBannerOptions()?.isCanLoop() ?: false
            if (!canLoop) {
                if (currentPosition == 0 && endY - startY > 0) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    parent.requestDisallowInterceptTouchEvent(
                        currentPosition != getData().size - 1
                                || endY - startY >= 0
                    )
                }
            } else {
                parent.requestDisallowInterceptTouchEvent(true)
            }
        } else if (disX > disY) {
            parent.requestDisallowInterceptTouchEvent(false)
        }
    }

    private fun onHorizontalActionMove(endX: Int, disX: Int, disY: Int) {
        if (disX > disY) {
            val canLoop: Boolean = mBannerManager.getBannerOptions()?.isCanLoop() ?: false
            if (!canLoop) {
                if (currentPosition == 0 && endX - startX > 0) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    parent.requestDisallowInterceptTouchEvent(
                        (currentPosition != getData().size - 1
                                || endX - startX >= 0)
                    )
                }
            } else {
                parent.requestDisallowInterceptTouchEvent(true)
            }
        } else if (disY > disX) {
            parent.requestDisallowInterceptTouchEvent(false)
        }
    }

    private fun pageScrollStateChanged(state: Int) {
        mIndicatorView?.onPageScrollStateChanged(state)
        onPageChangeCallback?.onPageScrollStateChanged(state)
    }

    private fun pageSelected(position: Int) {
        val size: Int = mBannerPagerAdapter?.getListSize() ?: 0
        val canLoop: Boolean = mBannerManager.getBannerOptions()?.isCanLoop() ?: false
        currentPosition = BannerUtils.getRealPosition(position, size)
        val needResetCurrentItem =
            (size > 0) && canLoop && (position == 0 || position == MAX_VALUE - 1)
        if (needResetCurrentItem) {
            resetCurrentItem(currentPosition)
        }
        onPageChangeCallback?.onPageSelected(currentPosition)
        mIndicatorView?.onPageSelected(currentPosition)
    }

    private fun pageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val listSize: Int = mBannerPagerAdapter?.getListSize() ?: 0
        val realPosition: Int = BannerUtils.getRealPosition(position, listSize)
        if (listSize > 0) {
            onPageChangeCallback?.onPageScrolled(
                realPosition,
                positionOffset,
                positionOffsetPixels
            )
            mIndicatorView?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
        }
    }

    private fun handlePosition() {
        if ((mBannerPagerAdapter != null) && ((mBannerPagerAdapter?.getListSize() ?: 0) > 1) && isAutoPlay()) {
            var currentItem = (mViewPager?.currentItem ?: 0) + 1
            if (currentItem >= (mViewPager?.adapter?.itemCount ?: MAX_VALUE)) {
                currentItem = 0
            }
            mViewPager?.currentItem = currentItem
            mHandler.postDelayed(mRunnable, getInterval())
        }
    }

    private fun initBannerData(isInitCurrent: Boolean = true) {
        val list: List<T>? = mBannerPagerAdapter?.getData()
        if (list != null) {
            setIndicatorValues(list)
            setupViewPager(list, isInitCurrent)
            initRoundCorner()
        }
    }

    private fun setIndicatorValues(list: List<T>) {
        val bannerOptions: BannerOptions = mBannerManager.getBannerOptions()
        mIndicatorLayout?.visibility = bannerOptions.getIndicatorVisibility()
        bannerOptions.resetIndicatorOptions()
        if (!isCustomIndicator || null == mIndicatorView) {
            mIndicatorView = IndicatorView(context)
        }
        initIndicator(bannerOptions.getIndicatorOptions(), list)
    }

    private fun initIndicator(indicatorOptions: IndicatorOptions, list: List<T>) {
        if ((mIndicatorView is View) && (mIndicatorView as View?)?.parent == null) {
            mIndicatorLayout?.removeAllViews()
            mIndicatorLayout?.addView(mIndicatorView as View?)
            initIndicatorSliderMargin()
            initIndicatorGravity()
        }
        mIndicatorView?.setInficatorOptions(indicatorOptions)
        indicatorOptions.pageSize = list.size
        mIndicatorView?.notifyDataChanged()
    }

    private fun initIndicatorGravity() {
        val layoutParams = (mIndicatorView as View?)?.layoutParams as android.view.ViewGroup.LayoutParams
        when (mBannerManager.getBannerOptions().getIndicatorGravity()) {
            CENTER -> layoutParams.addRule(
                CENTER_HORIZONTAL
            )
            START -> layoutParams.addRule(ALIGN_PARENT_RIGHT)
            END -> layoutParams.addRule(ALIGN_PARENT_RIGHT)
            else -> {}

        }
    }

    private fun initIndicatorSliderMargin() {
        val layoutParams = (mIndicatorView as View?)?.layoutParams as MarginLayoutParams
        val indicatorMargin: BannerOptions.IndicatorMargin? =
            mBannerManager.getBannerOptions().getIndicatorMargin()
        if (indicatorMargin == null) {
            val dp10: Int = BannerUtils.dp2px(10f)
        } else {
            layoutParams.setMargins(
                indicatorMargin.left, indicatorMargin.top,
                indicatorMargin.right, indicatorMargin.bottom
            )
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        val roundRectRadiusArray: FloatArray? =
            mBannerManager.getBannerOptions().getRoundRectRadiusArray()
        if ((mRadiusRectF != null) && (mRadiusPath != null) && (roundRectRadiusArray != null)) {
            mRadiusRectF?.right = this.width.toFloat()
            mRadiusRectF?.bottom = this.height.toFloat()
            mRadiusPath?.addRoundRect(mRadiusRectF!!, roundRectRadiusArray, Path.Direction.CW)
            canvas.clipPath(mRadiusPath!!)
        }
        super.dispatchDraw(canvas)
    }

    private fun initRoundCorner() {
        val roundCorner: Int = mBannerManager.getBannerOptions().getRoundRectRadius()
        if (roundCorner > 0) {
            ViewUtils.setClipViewCornerRadius(this, roundCorner)
        }
    }

    private fun setupViewPager(list: List<T>, isInitCurrent: Boolean = true) {
        if (mBannerPagerAdapter == null) {
            throw NullPointerException("You must set adapter for BannerViewPager")
        }
        val bannerOptions: BannerOptions = mBannerManager.getBannerOptions()
    }

}