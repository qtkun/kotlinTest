package com.qtk.kotlintest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.RvAdapter
import com.qtk.kotlintest.adapter.TopLevelAdapter
import com.qtk.kotlintest.base.BaseAdapter
import com.qtk.kotlintest.base.update
import com.qtk.kotlintest.databinding.ActivityRecyclerBinding
import com.qtk.kotlintest.domain.data.server.BrandBean
import com.qtk.kotlintest.extensions.inflate
import com.qtk.kotlintest.retrofit.data.NodeBean
import com.qtk.kotlintest.retrofit.data.TopNodeBean
import com.qtk.kotlintest.widget.BrandDecoration

class RecyclerActivity: AppCompatActivity(R.layout.activity_recycler) {
    val binding by inflate<ActivityRecyclerBinding>()

    val adapter by lazy {
        TopLevelAdapter(list)
    }

    val list = mutableListOf(
        TopNodeBean("常驻功能", true, mutableListOf(
            NodeBean("企业设置", true, true, mutableListOf(
                NodeBean("企业基础信息", true, null, mutableListOf(
                    NodeBean("入口权限", true, null, mutableListOf())
                )),
                NodeBean("组织架构", true, null, mutableListOf(
                    NodeBean("查看", true, null, mutableListOf()),
                    NodeBean("添加", true, null, mutableListOf()),
                    NodeBean("修改", true, null, mutableListOf()),
                    NodeBean("删除", true, null, mutableListOf())
                )),
                NodeBean("角色管理", true, null, mutableListOf(
                    NodeBean("查看", true, null, mutableListOf()),
                    NodeBean("添加", true, null, mutableListOf()),
                    NodeBean("修改", true, null, mutableListOf()),
                    NodeBean("删除", true, null, mutableListOf())
                )),
                NodeBean("信息分类设置", true, null, mutableListOf(
                    NodeBean("入口权限", true, null, mutableListOf())
                ))
            )),
            NodeBean("报表", true, true, mutableListOf(
                NodeBean("入口权限", true, null, mutableListOf())
            ))
        )),
        TopNodeBean("一级功能模块1", true, mutableListOf(
            NodeBean("二级功能模块1", true, null, mutableListOf(
                NodeBean("查看", true, null, mutableListOf()),
                NodeBean("添加", true, null, mutableListOf()),
                NodeBean("修改", true, null, mutableListOf()),
                NodeBean("删除", true, null, mutableListOf())
            )),
            NodeBean("二级功能模块2", true, null, mutableListOf(
                NodeBean("查看", true, null, mutableListOf()),
                NodeBean("添加", true, null, mutableListOf()),
                NodeBean("修改", true, null, mutableListOf()),
                NodeBean("删除", true, null, mutableListOf())
            ))
        )),
        TopNodeBean("一级功能模块2", true, mutableListOf(
            NodeBean("二级功能模块1", true, null, mutableListOf(
                NodeBean("查看", true, null, mutableListOf()),
                NodeBean("添加", true, null, mutableListOf()),
                NodeBean("修改", true, null, mutableListOf()),
                NodeBean("删除", true, null, mutableListOf())
            )),
            NodeBean("二级功能模块2", true, null, mutableListOf(
                NodeBean("查看", true, null, mutableListOf()),
                NodeBean("添加", true, null, mutableListOf()),
                NodeBean("修改", true, null, mutableListOf()),
                NodeBean("删除", true, null, mutableListOf())
            ))
        ))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding.rv) {
            layoutManager = LinearLayoutManager(this@RecyclerActivity).apply {
                initialPrefetchItemCount = 4
            }
            adapter = this@RecyclerActivity.adapter
        }

    }
}