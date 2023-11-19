package com.example.lib_banner.annotation

import androidx.annotation.IntDef
import com.example.lib_banner.mode.IndicatorOrientation

/**
 * 指示器方向
 */
@IntDef(
    IndicatorOrientation.INDICATOR_HORIZONTAL, IndicatorOrientation.INDICATOR_VERTICAL,
    IndicatorOrientation.INDICATOR_RTL
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
annotation class AIndicatorOrientation
