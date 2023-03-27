package com.xandone.basefilter.filter

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    /**
     * 元数据
     */
    private var mFilterList: Array<out FilterInfo>? = null

    /**
     * 元数据的json形式
     */
    private var mFilterOriginalJson: String? = null

    /**
     * 确定按钮回调函数
     */
    private var mCommitCallback: ((isChanged: Boolean, filterList: Array<out FilterInfo>?) -> Unit)? =
        null

    /**
     * 重置按钮回调函数
     */
    private var mResetCallback: (() -> Unit)? = null

    @LayoutRes
    private var vFilterSearch: Int = R.layout.v_filter_search

    @LayoutRes
    private var vFilterSearchSpinner: Int = R.layout.v_filter_search_spinner

    @LayoutRes
    private var vFilterGrid: Int = R.layout.v_filter_grid


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
            FilterSearch -> {
                val view = layoutInflater.inflate(vFilterSearch, null)
                contentLl.addView(view)

                view.findViewById<TextView>(R.id.title_tv).text = filterInfo.title
                val searchEt = view.findViewById<AppCompatEditText>(R.id.search_et)
                searchEt.hint = filterInfo.items[1].value
                searchEt.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        filterInfo.items[0].value = s.toString()
                    }

                })
            }
            FilterSearchSpinner -> {
                val view = layoutInflater.inflate(vFilterSearchSpinner, null)
                contentLl.addView(view)

                val tv = view.findViewById<TextView>(R.id.spinner_tv)
                view.findViewById<TextView>(R.id.title_tv).text = filterInfo.title
                val searchEt = view.findViewById<AppCompatEditText>(R.id.search_et)
                searchEt.hint = filterInfo.items[1].value

                val list = filterInfo.items
                    .filterIndexed { index, _ -> index > 1 }
                    .map {
                        it.value
                    }

                tv.setOnClickListener {
                    showPopue(tv, list.toTypedArray(), filterInfo.items)
                }

                searchEt.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        filterInfo.items[0].value = s.toString()
                    }

                })
            }
            FilterGrid -> {
                val view = layoutInflater.inflate(vFilterGrid, null)
                contentLl.addView(view)
                view.findViewById<TextView>(R.id.title_tv).text = filterInfo.title
                val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
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

    private fun removeItem() {
        contentLl.removeAllViews()
    }

    private fun reset() {
        removeItem()
        mFilterList = json2Array(mFilterOriginalJson, Array<FilterInfo>::class.java)
        mFilterList?.forEach {
            addItem(it)
        }
        mResetCallback?.invoke()
    }

    private fun commit() {
        mCommitCallback?.invoke(isChanged(), mFilterList)
    }


    /**
     * 数据是否进行了操作
     */
    private fun isChanged(): Boolean {
        val json = obj2Json(mFilterList)
        return json != mFilterOriginalJson
    }


    class Holder(itemView: View, private val list: List<FilterItem>) :
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

    private fun showPopue(tv: TextView, list: Array<String>, list2: List<FilterItem>) {
        XPopup.Builder(activity)
            .isDarkTheme(false)
            .hasShadowBg(false)
            .atView(tv)
            .popupWidth(tv.width)
            .maxHeight(dp2px(requireActivity(), 40 * 4f))
            .popupAnimation(PopupAnimation.ScrollAlphaFromTop)
            .asAttachList(list, null, { position, text ->
                tv.text = text
                list2.forEach {
                    it.isSelect = false
                }
                list2[position + 2].isSelect = true
            }, 0, R.layout._xpopup_adapter_text)
            .show()
    }

    /**
     * 绑定视图数据
     */
    fun bindItem(vararg info: FilterInfo): FilterFragment {
        this.mFilterList = info
        this.mFilterOriginalJson = obj2Json(mFilterList)
        return this
    }


    fun setCommitCallBack(
        callback: (isChanged: Boolean, filterList: Array<out FilterInfo>?) -> Unit
    ): FilterFragment {
        this.mCommitCallback = callback
        return this
    }

    fun setResetCallback(callback: () -> Unit): FilterFragment {
        this.mResetCallback = callback
        return this
    }


    /**
     * 修改布局
     */
    fun changeResLayout(type: FilterType, @LayoutRes layoutId: Int): FilterFragment {

        when (type) {
            FilterSearch -> vFilterSearch = layoutId
            FilterSearchSpinner -> vFilterSearchSpinner = layoutId
            FilterGrid -> vFilterGrid = layoutId
        }

        return this

    }

    companion object {
        fun getInstance(): FilterFragment {
            return FilterFragment()
        }
    }


}