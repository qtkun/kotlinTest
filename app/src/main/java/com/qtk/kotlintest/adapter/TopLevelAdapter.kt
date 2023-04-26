package com.qtk.kotlintest.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtk.kotlintest.base.base.BaseAdapter
import com.qtk.kotlintest.databinding.ItemTopLevelBinding
import com.qtk.kotlintest.extensions.singleClick
import com.qtk.kotlintest.retrofit.data.TopNodeBean

class TopLevelAdapter(items: MutableList<TopNodeBean>): BaseAdapter<TopNodeBean, ItemTopLevelBinding>(items) {
    override fun ItemTopLevelBinding.bindView(position: Int, item: TopNodeBean) {
        tvName.text = item.name
        tvExpand.text = if (item.isExpand) "收起" else "展开"
        ivExpand.rotation = if (item.isExpand) -90f else 90f
        rvSecond.isVisible = item.isExpand
        if (rvSecond.adapter != null) {
            rvSecond.adapter!!.notifyDataSetChanged()
        } else {
            rvSecond.apply {
                layoutManager = LinearLayoutManager(root.context)
                adapter = SecondLevelAdapter(item.children)
                setItemViewCacheSize(200)
                setHasFixedSize(true)
            }
        }
        llExpand.singleClick {
            item.isExpand = !item.isExpand
            notifyItemChanged(position)
        }
    }
}