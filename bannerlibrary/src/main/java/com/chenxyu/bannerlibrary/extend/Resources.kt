package com.chenxyu.bannerlibrary.extend

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.DrawableRes

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/9/3 20:10
 * @Description:
 * @Version:       1.0
 */

/**
 * 获取Drawable资源
 */
fun Context.getDrawable2(@DrawableRes resId: Int, theme: Resources.Theme? = null): Drawable =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) resources.getDrawable(resId, theme) else resources.getDrawable(resId)