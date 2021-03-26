package com.qtk.kotlintest.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.PokemonAdapter
import com.qtk.kotlintest.adapter.LoadMoreAdapter
import com.qtk.kotlintest.databinding.FragmentPokemonListBinding
import com.qtk.kotlintest.extensions.bindView
import com.qtk.kotlintest.view_model.PokemonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject

@AndroidEntryPoint
class PokemonFragment : Fragment(R.layout.fragment_pokemon_list) {
    private val mViewModel by inject<PokemonViewModel>()
    private var adapter: PokemonAdapter = PokemonAdapter {_,_ -> }

    private val binding by bindView<FragmentPokemonListBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.getPokemon()
            .observe(viewLifecycleOwner, {
                lifecycleScope.launchWhenCreated {
                    adapter.submitData(it)
                }
            })
        with(binding) {
            pokemonList.adapter = adapter.withLoadStateFooter(LoadMoreAdapter {
                adapter.retry()
            })
            pokemonList.layoutManager = LinearLayoutManager(context)

            lifecycleScope.launchWhenCreated {
                adapter.addLoadStateListener {
                    mViewModel.refreshListener(it)
                }
            }
        }
    }
}