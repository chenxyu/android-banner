package com.chenxyu.bannerlibrary

import android.view.Gravity

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/8/19 15:49
 * @Description:   默认指示器
 * @Version:       1.0
 * @param indicatorWH   指示器的宽高
 * @param gravity       指示器的位置
 */
class DefaultIndicator(indicatorWH: Int = 7, gravity: Int = Gravity.CENTER) : Indicator() {
    init {
        // 继承Indicator在init里设置更多属性
        indicatorGravity = gravity
        indicatorWidth = indicatorWH
        indicatorHeight = indicatorWH
        indicatorSelectedW = indicatorWH
        indicatorSelectedH = indicatorWH
    }

    override fun getNormalDrawable(): Int {
        return R.drawable.indicator_gray
    }

    override fun getSelectedDrawable(): Int {
        return R.drawable.indicator_white
    }
}