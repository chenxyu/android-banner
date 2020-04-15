package com.chenxyu.bannerlibrary.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.chenxyu.bannerlibrary.BannerView
import kotlin.math.abs

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/4/15 21:38
 * @Description:
 * @Version:       1.0
 */
class RotationPageTransformer(private val orientation: Int) : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            val absPos = abs(position)
            rotation = position * 360
            when(orientation){
                BannerView.HORIZONTAL->{
                    translationX = absPos * 350f
                }
                BannerView.VERTICAL->{
                    translationY = absPos * 500f
                }
            }
            val scale = if (absPos > 1) 0F else 1 - absPos
            scaleX = scale
            scaleY = scale
            translationY = absPos * 500f
            translationX = absPos * 350f
        }
    }
}