package com.qtk.kotlintest.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.GoodsAdapter
import com.qtk.kotlintest.adapter.LoadMoreAdapter
import com.qtk.kotlintest.view_model.GoodsViewModel
import kotlinx.android.synthetic.main.fragment_goods_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoodsFragment : Fragment(R.layout.fragment_goods_list) {
    private val mViewModel by viewModel<GoodsViewModel>()
    private var adapter: GoodsAdapter = GoodsAdapter { }

    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel.getGoods(0, "1", "createtime")
            .observe(this, Observer {
            lifecycleScope.launchWhenCreated {
                adapter.submitData(it)
            }
        })
        goods_list.adapter = adapter.withLoadStateFooter(LoadMoreAdapter {
            adapter.retry()
        })
        goods_list.layoutManager = LinearLayoutManager(context)

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