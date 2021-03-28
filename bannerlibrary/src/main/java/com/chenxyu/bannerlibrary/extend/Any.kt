package com.chenxyu.bannerlibrary.extend

/**
 * @Author:        ChenXingYu
 * @CreateDate:    2021/3/28 11:22
 * @Description:
 * @Version:       1.0
 */

/**
 * 简化类型转换，避免空指针，安全的类型转换
 */
internal inline fun <reified T> Any.az(): T? = if (this is T) this else null