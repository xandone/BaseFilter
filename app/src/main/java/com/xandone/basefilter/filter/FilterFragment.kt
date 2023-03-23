package com.xandone.basefilter.filter

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.xandone.basefilter.R
import com.xandone.basefilter.filter.FilterType.*

/**
 * @author: xiao
 * created on: 2023/3/22 10:34
 * description:
 */
class FilterFragment : Fragment() {

    private lateinit var contentLl: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.frag_filter, container, false)
        contentLl = rootView.findViewById(R.id.content_ll)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFilterList?.forEach {
            addItem(it)
        }

        val resetTv = view.findViewById<TextView>(R.id.reset_tv)
        val commitTv = view.findViewById<TextView>(R.id.commit_tv)

        setClick(resetTv, commitTv) { v ->
            when (v) {
                resetTv -> {
                    reset()
                }
                commitTv -> {
                    commit()
                }
            }
        }
    }

    private fun addItem(filterInfo: FilterInfo) {
        when (filterInfo.type) {
            filterSearch -> {
                val view = layoutInflater.inflate(R.layout.v_filter_search, null)
                contentLl.addView(view)
            }
            filterSearchSpinner -> {
                val view = layoutInflater.inflate(R.layout.v_filter_search_spinner, null)
                val tv = view.findViewById<TextView>(R.id.spinner_tv)
                val list = filterInfo.items.map {
                    it.value
                }
                tv.setOnClickListener {
                    showPopue(tv, list.toTypedArray())
                }
                contentLl.addView(view)
            }
            filterGrid -> {
                val recyclerView =
                    layoutInflater.inflate(R.layout.v_filter_grid, null) as RecyclerView
                val adapter = object : RecyclerView.Adapter<Holder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
                        return Holder(
                            layoutInflater.inflate(R.layout.item_grid, parent, false),
                            filterInfo.items
                        )
                    }

                    override fun onBindViewHolder(holder: Holder, position: Int) {
                        holder.bindView(filterInfo.items[position], this)
                    }

                    override fun getItemCount(): Int {
                        return filterInfo.items.size
                    }
                }
                contentLl.addView(recyclerView)
                recyclerView.apply {
                    this.adapter = adapter
                    layoutManager = GridLayoutManager(activity, 3)
                    addItemDecoration(object : RecyclerView.ItemDecoration() {
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            super.getItemOffsets(outRect, view, parent, state)
                            outRect.top = dp2px(activity!!, 10f)
                            outRect.right = dp2px(activity!!, 10f)
                        }
                    })
                }

            }

        }
    }

    fun removeItem() {
        contentLl.removeAllViews()
    }

    fun reset() {
        removeItem()
        mFilterList = json2Array(mFilterOriginalJson, Array<FilterInfo>::class.java)
        mFilterList?.forEach {
            addItem(it)
        }
    }

    fun commit() {
        //TODO
    }

    fun bindItem(vararg info: FilterInfo) {
        this.mFilterList = info
        this.mFilterOriginalJson = obj2Json(mFilterList)
    }

    private var mFilterList: Array<out FilterInfo>? = null
    private var mFilterOriginalJson: String? = null


    inner class Holder(itemView: View, private val list: List<FilterItem>) :
        RecyclerView.ViewHolder(itemView) {

        fun bindView(bean: FilterItem, adapter: RecyclerView.Adapter<*>) {
            val tv = itemView.findViewById<TextView>(R.id.tv1)
            tv.text = bean.value
            tv.isSelected = bean.isSelect

            itemView.setOnClickListener {
                list.forEach {
                    it.isSelect = false
                }
                bean.isSelect = true
                adapter.notifyDataSetChanged()
            }

        }
    }

    private fun showPopue(tv: TextView, list: Array<String>) {
        XPopup.Builder(activity)
            .isDarkTheme(false)
            .hasShadowBg(false)
            .atView(tv)
            .popupWidth(tv.width)
            .maxHeight(dp2px(requireActivity(), 40 * 4f))
            .popupAnimation(PopupAnimation.ScrollAlphaFromTop)
            .asAttachList(list, null, { _, text ->
                tv.text = text
            }, 0, R.layout._xpopup_adapter_text)
            .show()
    }

    companion object {
        fun getInstance(): FilterFragment {
            return FilterFragment()
        }
    }

    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }


}