package com.xandone.basefilter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xandone.basefilter.databinding.ActMainBinding
import com.xandone.basefilter.filter.FilterFragment
import com.xandone.basefilter.filter.FilterInfo
import com.xandone.basefilter.filter.FilterItem
import com.xandone.basefilter.filter.FilterType

/**
 * @author: xiao
 * created on: 2023/3/22 10:09
 * description:
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActMainBinding.inflate(layoutInflater).root)

        val fragment = FilterFragment.getInstance()
        fragment.bindItem(
            FilterInfo(
                FilterType.filterSearch,
                mutableListOf(
                    FilterItem(id = 1, value = "数据1"),
                    FilterItem(id = 2, value = "数据2")
                )
            ),
            FilterInfo(
                FilterType.filterSearchSpinner,
                mutableListOf(
                    FilterItem(id = 1, value = "数据1"),
                    FilterItem(id = 2, value = "数据2")
                )
            ),
            FilterInfo(
                FilterType.filterGrid,
                mutableListOf(
                    FilterItem(id = 1, value = "数据1"),
                    FilterItem(id = 2, value = "数据2"),
                    FilterItem(id = 2, value = "数据3"),
                    FilterItem(id = 2, value = "数据4", true)
                )
            )
        )
        supportFragmentManager.beginTransaction().add(R.id.drawer_content, fragment)
            .commitAllowingStateLoss()
    }
}