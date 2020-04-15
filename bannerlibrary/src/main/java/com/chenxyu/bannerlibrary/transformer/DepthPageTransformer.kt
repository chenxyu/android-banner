package com.chenxyu.bannerlibrary.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.chenxyu.bannerlibrary.BannerView
import kotlin.math.abs

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/4/15 22:58
 * @Description:
 * @Version:       1.0
 */
class DepthPageTransformer(private val orientation: Int) : ViewPager2.PageTransformer {
    companion object {
        const val MIN_SCALE = 0.75f
    }

    override fun transformPage(page: View, position: Float) {
        page.apply {
            val pageWidth = width
            val pageHeight = height

            when (orientation) {
                BannerView.HORIZONTAL -> {
                    when {
                        position < -1 -> { // [-Infinity,-1)
                            // This page is way off-screen to the left.
                            alpha = 0f
                        }
                        position <= 0 -> { // [-1,0]
                            // Use the default slide transition when moving to the left page
                            alpha = 1f
                            translationX = 0f
                            scaleX = 1f
                            scaleY = 1f
                        }
                        position <= 1 -> { // (0,1]
                            // Fade the page out.
                            alpha = 1 - position
                            // Counteract the default slide transition
                            translationX = pageWidth * -position
                            // Scale the page down (between MIN_SCALE and 1)
                            val scaleFactor = (MIN_SCALE
                                    + (1 - MIN_SCALE) * (1 - abs(position)))
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                        }
                        else -> { // (1,+Infinity]
                            // This page is way off-screen to the right.
                            alpha = 0f
                        }
                    }
                }
                BannerView.VERTICAL -> {
                    when {
                        position < -1 -> { // [-Infinity,-1)
                            // This page is way off-screen to the left.
                            alpha = 0f
                        }
                        position <= 0 -> { // [-1,0]
                            // Use the default slide transition when moving to the left page
                            alpha = 1f
                            translationY = 0f
                            scaleX = 1f
                            scaleY = 1f
                        }
                        position <= 1 -> { // (0,1]
                            // Fade the page out.
                            alpha = 1 - position
                            // Counteract the default slide transition
                            translationY = pageHeight * -position
                            // Scale the page down (between MIN_SCALE and 1)
                            val scaleFactor = (MIN_SCALE
                                    + (1 - MIN_SCALE) * (1 - abs(position)))
                            scaleX = scaleFactor
                            scaleY = scaleFactor
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