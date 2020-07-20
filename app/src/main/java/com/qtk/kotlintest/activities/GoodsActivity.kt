package com.qtk.kotlintest.activities

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.GoodsAdapter
import com.qtk.kotlintest.adapter.LoadMoreAdapter
import com.qtk.kotlintest.view_model.GoodsViewModel
import kotlinx.android.synthetic.main.activity_goods_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.anko.find
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoodsActivity : AppCompatActivity(R.layout.activity_goods_list), ToolbarManager {
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    override val activity: Activity by lazy { this }
    private val mViewModel by viewModel<GoodsViewModel>()
    private var adapter: GoodsAdapter = GoodsAdapter { }

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.getGoods(0, "1", "createtime")
            .observe(this, Observer {
            lifecycleScope.launchWhenCreated {
                adapter.submitData(it)
            }
        })
        initToolbar()
        goods_list.adapter = adapter.withLoadStateFooter(LoadMoreAdapter {
            adapter.retry()
        })
        goods_list.layoutManager = LinearLayoutManager(this)
        attachToScroll(goods_list)
        toolbarTitle = "Goods"
        enableHomeAsUp { onBackPressed() }

        lifecycleScope.launchWhenCreated {
            /*adapter.loadStateFlow.collectLatest {
                if (it.refresh !is LoadState.Loading) {
                    goods_refresh.isRefreshing = false
                }
            }*/
            adapter.addLoadStateListener {
                when (it.refresh) {
                    is LoadState.Error -> goods_refresh.isRefreshing = false
                    is LoadState.Loading -> goods_refresh.isRefreshing = true
                    is LoadState.NotLoading -> goods_refresh.isRefreshing = false
                }
            }
        }

        goods_refresh.setOnRefreshListener {
            adapter.refresh()
        }
    }
}