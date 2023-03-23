package com.xandone.basefilter.filter

import android.view.View

/**
 * @author: xiao
 * created on: 2023/3/23 15:08
 * description:
 */

fun setClick(vararg views: View, block: (View) -> Unit) {
    views.forEach {
        it.setOnClickListener { v ->
            block.invoke(v)
        }
    }
}