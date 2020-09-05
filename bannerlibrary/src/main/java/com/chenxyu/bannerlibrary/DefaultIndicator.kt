package com.chenxyu.bannerlibrary

import android.view.Gravity
import androidx.annotation.DrawableRes

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/8/19 15:49
 * @Description:   默认指示器
 * @Version:       1.0
 * @param normalDrawable    默认Indicator
 * @param selectedDrawable  选中Indicator
 * @param margin            指示器外边距（DP）
 * @param gravity           指示器的位置
 */
class DefaultIndicator(@DrawableRes normalDrawable: Int? = R.drawable.indicator_gray,
                       @DrawableRes selectedDrawable: Int? = R.drawable.indicator_white,
                       margin: Int? = 4,
                       gravity: Int? = Gravity.CENTER
) : Indicator() {
    init {
        // 继承Indicator在init里设置更多属性
        normalDrawable?.let { indicatorNormalDrawable = it }
        selectedDrawable?.let { indicatorSelectedDrawable = it }
        margin?.let { indicatorMargin = it }
        gravity?.let { indicatorGravity = it }
    }
}