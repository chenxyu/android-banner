package com.chenxyu.bannerlibrary

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/8/19 15:49
 * @Description:   默认指示器
 * @Version:       1.0
 */
class DefaultIndicator : Indicator() {
    override fun getNormalIndicatorDrawable(): Int {
        return R.drawable.indicator_gray
    }

    override fun getSelectedIndicatorDrawable(): Int {
        return R.drawable.indicator_white
    }
}