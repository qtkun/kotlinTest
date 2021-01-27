package com.qtk.kotlintest.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.PokemonAdapter
import com.qtk.kotlintest.adapter.LoadMoreAdapter
import com.qtk.kotlintest.databinding.FragmentPokemonListBinding
import com.qtk.kotlintest.view_model.PokemonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class PokemonFragment : Fragment() {
    private val mViewModel by lazy { ViewModelProvider(this).get(PokemonViewModel::class.java) }
    private var adapter: PokemonAdapter = PokemonAdapter { }

    private var _binding: FragmentPokemonListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel.getPokemon()
            .observe(this, {
            lifecycleScope.launchWhenCreated {
                adapter.submitData(it)
            }
        })
        binding.pokemonList.adapter = adapter.withLoadStateFooter(LoadMoreAdapter {
            adapter.retry()
        })
        binding.pokemonList.layoutManager = LinearLayoutManager(context)

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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}