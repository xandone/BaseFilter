package com.xandone.basefilter

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.xandone.basefilter.databinding.ActMainBinding
import com.xandone.basefilter.filter.*

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
                FilterType.FilterSearch,
                mutableListOf(
                    FilterItem(id = 1, value = ""),
                    FilterItem(id = 2, value = "请输入..")
                ),
                "输入框标题"
            ),
            FilterInfo(
                FilterType.FilterSearchSpinner,
                mutableListOf(
                    FilterItem(id = 1, value = ""),
                    FilterItem(id = 2, value = "请输入.."),
                    FilterItem(id = 3, value = "数据1"),
                    FilterItem(id = 4, value = "数据2"),
                    FilterItem(id = 5, value = "数据3")
                ),
                "选择框标题"
            ),
            FilterInfo(
                FilterType.FilterGrid,
                mutableListOf(
                    FilterItem(id = 1, value = "数据1"),
                    FilterItem(id = 2, value = "数据2"),
                    FilterItem(id = 3, value = "数据3"),
                    FilterItem(id = 4, value = "数据4", true)
                ),
                "多选项标题"
            )
        ).changeResLayout(FilterType.FilterSearch, R.layout.v_filter_search2)
            .setCommitCallBack { isChanged, filterList ->
                Log.d("tag123", "isChanged=$isChanged")
                Log.d("tag123", "filterList=${obj2Json(filterList)}")
            }.setResetCallback {
                Log.d("tag123", "重置..")
            }

        supportFragmentManager.beginTransaction().add(R.id.drawer_content, fragment)
            .commitAllowingStateLoss()
    }
}