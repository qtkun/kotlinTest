package com.qtk.kotlintest.adapter

import androidx.databinding.ViewDataBinding
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.BaseAdapter
import com.qtk.kotlintest.base.BaseListAdapter
import com.qtk.kotlintest.databinding.ItemRvBinding
import com.qtk.kotlintest.domain.data.server.BrandBean

class RvAdapter(data:List<BrandBean>, itemClick : (BrandBean, ViewDataBinding) -> Unit) :
BaseAdapter<BrandBean, ItemRvBinding>(data, itemClick, R.layout.item_rv)