package com.chenxyu.bannerlibrary.extend

import android.content.Context

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/8/13 21:13
 * @Description:
 * @Version:       1.0
 */

/**
 * dp转px
 */
internal fun Float.dpToPx(context: Context): Float {
    val scale = context.resources.displayMetrics.density
    return this * scale + 0.5f
}

/**
 * dp转px
 */
internal fun Int.dpToPx(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}