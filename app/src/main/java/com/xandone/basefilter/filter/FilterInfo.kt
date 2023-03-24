package com.xandone.basefilter.filter

/**
 * @author: xiao
 * created on: 2023/3/22 14:16
 * description:
 */
data class FilterInfo(val type: FilterType, val items: List<FilterItem>, val title: String? = null)
