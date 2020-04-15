package com.chenxyu.bannerlibrary.listener

import android.view.View

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2020/4/15 11:46
 * @Description:
 * @Version:       1.0
 */
interface OnItemClickListener {
    /**
     * 点击
     * @param view 当前ItemView
     * @param position 当前Item位置（自定义Adapter获取的都是真实position）
     */
    fun onItemClick(view: View?, position: Int)
}