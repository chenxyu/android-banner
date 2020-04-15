package com.chenxyu.bannerlibrary.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.chenxyu.bannerlibrary.BannerView

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/4/15 17:47
 * @Description:
 * @Version:       1.0
 * @param orientation
 */
class ScalePageTransformer(private val orientation: Int) : ViewPager2.PageTransformer {
    companion object {
        const val MIN_SCALE = 0.9f
        const val LEFT = 0.8f
        const val RIGHT = 1 - LEFT
    }

    override fun transformPage(page: View, position: Float) {
        page.apply {
            val pageWidth = width
            val pageHeight = height
            pivotY = (pageHeight / 2).toFloat()
            pivotX = (pageWidth / 2).toFloat()

            when (orientation) {
                BannerView.HORIZONTAL -> {
                    when {
                        position < 0 -> {
                            val scaleFactor = (1 + position) * (1 - MIN_SCALE) + MIN_SCALE
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            pivotX = pageWidth * LEFT
                        }
                        position < 1 -> {
                            val scaleFactor = (1 - position) * (1 - MIN_SCALE) + MIN_SCALE
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            pivotX = (pageWidth / 2).toFloat()
                        }
                        else -> {
                            val scaleFactor = (1 - position) * (1 - MIN_SCALE) + MIN_SCALE
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            pivotX = pageWidth * RIGHT
                        }
                    }
                }
                BannerView.VERTICAL -> {
                    when {
                        position < 0 -> {
                            val scaleFactor = (1 + position) * (1 - MIN_SCALE) + MIN_SCALE
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            pivotY = pageHeight * LEFT
                        }
                        position < 1 -> {
                            val scaleFactor = (1 - position) * (1 - MIN_SCALE) + MIN_SCALE
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            pivotY = (pageHeight / 2).toFloat()
                        }
                        else -> {
                            val scaleFactor = (1 - position) * (1 - MIN_SCALE) + MIN_SCALE
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            pivotY = pageHeight * RIGHT
                        }
                    }
                }
            }
        }
    }

}