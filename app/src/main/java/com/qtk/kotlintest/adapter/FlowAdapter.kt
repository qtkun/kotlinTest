package com.qtk.kotlintest.adapter

import androidx.databinding.ViewDataBinding
import com.qtk.kotlintest.R
import com.qtk.kotlintest.base.BaseAdapter
import com.qtk.kotlintest.databinding.ItemFlowBinding

class FlowAdapter(data:List<String>?, itemClick : (String, ViewDataBinding) -> Unit) :
BaseAdapter<String, ItemFlowBinding>(data, itemClick, R.layout.item_flow)