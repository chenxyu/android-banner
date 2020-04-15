package com.chenxyu.bannerlibrary.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.chenxyu.bannerlibrary.BannerView
import kotlin.math.abs
import kotlin.math.max

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/4/16 0:14
 * @Description:
 * @Version:       1.0
 */
class ZoomOutPageTransformer(private val orientation: Int) : ViewPager2.PageTransformer {
    companion object {
        const val MIN_SCALE = 0.9f
        const val MIN_ALPHA = 0.5f
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
                        position < -2 -> { // [-Infinity,-1)
                            // This page is way off-screen to the left.
                            alpha = 0f
                        }
                        position <= 2 -> { // [-2,-1,1,2]
                            // Modify the default slide transition to shrink the page as well
                            val scaleFactor = max(MIN_SCALE, 1 - abs(position))
                            val vertMargin = pageHeight * (1 - scaleFactor) / 2
                            val horzMargin = pageWidth * (1 - scaleFactor) / 2
                            translationX = if (position < 0) {
                                horzMargin - vertMargin / 2
                            } else {
                                -horzMargin + vertMargin / 2
                            }
                            // Scale the page down (between MIN_SCALE and 1)
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            // Fade the page relative to its size.
                            alpha = (MIN_ALPHA + (scaleFactor - MIN_SCALE)
                                    / (1 - MIN_SCALE) * (1 - MIN_ALPHA))
                        }
                        else -> { // (1,+Infinity]
                            // This page is way off-screen to the right.
                            alpha = 0f
                        }
                    }
                }
                BannerView.VERTICAL -> {
                    when {
                        position < -2 -> { // [-Infinity,-1)
                            // This page is way off-screen to the left.
                            alpha = 0f
                        }
                        position <= 2 -> { // [-2,-1,1,2]
                            // Modify the default slide transition to shrink the page as well
                            val scaleFactor = max(MIN_SCALE, 1 - abs(position))
                            val vertMargin = pageWidth * (1 - scaleFactor) / 2
                            val horzMargin = pageHeight * (1 - scaleFactor) / 2
                            translationY = if (position < 0) {
                                horzMargin - vertMargin / 2
                            } else {
                                -horzMargin + vertMargin / 2
                            }
                            // Scale the page down (between MIN_SCALE and 1)
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            // Fade the page relative to its size.
                            alpha = (MIN_ALPHA + (scaleFactor - MIN_SCALE)
                                    / (1 - MIN_SCALE) * (1 - MIN_ALPHA))
                        }
                        else -> { // (1,+Infinity]
                            // This page is way off-screen to the right.
                            alpha = 0f
                        }
                    }
                }
            }
        }
    }

}