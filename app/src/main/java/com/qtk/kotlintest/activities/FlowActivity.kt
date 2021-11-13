package com.qtk.kotlintest.activities

import android.os.Bundle
import com.google.android.flexbox.FlexboxLayoutManager
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.FlowAdapter
import com.qtk.kotlintest.base.BaseActivity
import com.qtk.kotlintest.databinding.ActivityFlowLayoutBinding

class FlowActivity: BaseActivity<ActivityFlowLayoutBinding>(R.layout.activity_flow_layout) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.flowRv.layoutManager = FlexboxLayoutManager(this)
        val data = listOf(
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
            "AJ1", "Dunk Low", "Dunk High", "Dunk", "李宁", "sfsfsfsfsdfsdf", "sfd", "ggdgsg", "ds",
        )
        binding.flowRv.adapter = FlowAdapter(data) { _, _ -> }
    }
}