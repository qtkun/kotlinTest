package com.qtk.kotlintest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.RvAdapter
import com.qtk.kotlintest.base.BaseAdapter
import com.qtk.kotlintest.base.update
import com.qtk.kotlintest.databinding.ActivityRecyclerBinding
import com.qtk.kotlintest.domain.data.server.BrandBean
import com.qtk.kotlintest.extensions.inflate
import com.qtk.kotlintest.widget.BrandDecoration

class RecyclerActivity: AppCompatActivity(R.layout.activity_recycler) {
    val binding by inflate<ActivityRecyclerBinding>()

    val adapter by lazy {
        RvAdapter(list) { item, _ ->
            selected(item)
        }
    }

    private fun selected(item: BrandBean) {
        list.forEach {
            it.selected = item === it
        }
        adapter.notifyDataSetChanged()
    }

    val list = mutableListOf(
        BrandBean("YEEZY", true),
        BrandBean("Nike"),
        BrandBean("Li ning"),
        BrandBean("Jordan"),
        BrandBean("Adidas"),
        BrandBean("PUMA"),
        BrandBean("Nike"),
        BrandBean("Li ning"),
        BrandBean("Jordan"),
        BrandBean("Adidas"),
        BrandBean("PUMA"),
        BrandBean("Nike"),
        BrandBean("Li ning"),
        BrandBean("Jordan"),
        BrandBean("Adidas"),
        BrandBean("PUMA"),
        BrandBean("Nike"),
        BrandBean("Li ning"),
        BrandBean("Jordan"),
        BrandBean("Adidas"),
        BrandBean("PUMA"),
        BrandBean("Nike"),
        BrandBean("Li ning"),
        BrandBean("Jordan"),
        BrandBean("Adidas"),
        BrandBean("PUMA"),
        BrandBean("Nike"),
        BrandBean("Li ning"),
        BrandBean("Jordan"),
        BrandBean("Adidas"),
        BrandBean("PUMA"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding.rv) {
            layoutManager = LinearLayoutManager(this@RecyclerActivity)
            adapter = this@RecyclerActivity.adapter
            addItemDecoration(BrandDecoration())
        }

    }
}