package com.chenxyu.bannerlibrary

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/3/2 16:35
 * @Description:
 * @Version:       1.0
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ScaleInTransformer : ViewPager2.PageTransformer {
    private val mMinScale = DEFAULT_MIN_SCALE

    companion object {
        const val DEFAULT_MIN_SCALE = 0.85f
        const val DEFAULT_CENTER = 0.5f
    }

    override fun transformPage(view: View, position: Float) {
        view.elevation = -abs(position)
        val pageWidth = view.width
        val pageHeight = view.height

        view.pivotY = (pageHeight / 2).toFloat()
        view.pivotX = (pageWidth / 2).toFloat()
        if (position < -1) {
            view.scaleX = mMinScale
            view.scaleY = mMinScale
            view.pivotX = pageWidth.toFloat()
        } else if (position <= 1) {
            if (position < 0) {
                val scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageWidth * (DEFAULT_CENTER + DEFAULT_CENTER * -position)
            } else {
                val scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageWidth * ((1 - position) * DEFAULT_CENTER)
            }
        } else {
            view.pivotX = 0f
            view.scaleX = mMinScale
            view.scaleY = mMinScale
        }
    }
}