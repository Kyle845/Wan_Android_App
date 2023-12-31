package com.example.lib_banner.annotation

import android.annotation.SuppressLint
import androidx.annotation.IntDef
import com.example.lib_banner.mode.IndicatorGravity
import java.lang.annotation.ElementType
import java.lang.annotation.Target

/**
 * 指示器位置
 */
@IntDef(IndicatorGravity.CENTER, IndicatorGravity.START, IndicatorGravity.END)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(ElementType.PARAMETER)
annotation class AIndicatorGravity()
