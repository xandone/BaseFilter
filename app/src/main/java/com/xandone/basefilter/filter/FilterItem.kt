package com.xandone.basefilter.filter

/**
 * @author: xiao
 * created on: 2023/3/22 14:14
 * description:
 */
data class FilterItem(
    val id: Int = 0,
    val value: String,
    var isSelect: Boolean = false
)