package com.example.lib_banner.drawer

import android.graphics.Canvas

import com.example.lib_banner.options.IndicatorOptions

/**
 * DashDrawer
 */
class DashDrawer internal constructor(indicatorOptions: IndicatorOptions) : RectDrawer(
    indicatorOptions
) {

    override fun drawDash(canvas: Canvas) {
        canvas.drawRect(mRectF, mPaint)
    }
}
