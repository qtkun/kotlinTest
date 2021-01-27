package com.qtk.kotlintest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtk.kotlintest.adapter.PokemonAdapter
import com.qtk.kotlintest.adapter.LoadMoreAdapter
import com.qtk.kotlintest.databinding.ActivityMotionBinding
import com.qtk.kotlintest.view_model.PokemonViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel

@AndroidEntryPoint
class MotionActivity : AppCompatActivity() {
    //通过koin注入
    private val mViewModel by viewModel<PokemonViewModel>()
    //通过hilt注入
//    private val mViewModel by lazy { ViewModelProvider(this).get(PokemonViewModel::class.java) }
    private var adapter: PokemonAdapter = PokemonAdapter { }

    private val binding by lazy { ActivityMotionBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mViewModel.getPokemon().observe(this, {
                lifecycleScope.launchWhenCreated {
                    adapter.submitData(it)
                }
            })
        binding.pokemonList.adapter = adapter.withLoadStateFooter(LoadMoreAdapter {
            adapter.retry()
        })
        binding.pokemonList.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launchWhenCreated {
            adapter.addLoadStateListener {
                when (it.refresh) {
                    is LoadState.Error -> binding.pokemonRefresh.isRefreshing = false
                    is LoadState.Loading -> binding.pokemonRefresh.isRefreshing = true
                    is LoadState.NotLoading -> binding.pokemonRefresh.isRefreshing = false
                }
            }
        }

        binding.pokemonRefresh.setOnRefreshListener {
            adapter.refresh()
        }

        binding.back.setOnClickListener { finish() }
    }
}