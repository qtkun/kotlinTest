package com.qtk.kotlintest.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.PokemonAdapter
import com.qtk.kotlintest.adapter.LoadMoreAdapter
import com.qtk.kotlintest.base.BaseActivity
import com.qtk.kotlintest.base.initLoading
import com.qtk.kotlintest.databinding.ActivityMotionBinding
import com.qtk.kotlintest.extensions.inflate
import com.qtk.kotlintest.extensions.toJson
import com.qtk.kotlintest.extensions.toJsonList
import com.qtk.kotlintest.retrofit.data.PokemonBean
import com.qtk.kotlintest.view_model.PokemonViewModel
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@AndroidEntryPoint
class MotionActivity : BaseActivity<ActivityMotionBinding>(R.layout.activity_motion) {
    //通过koin注入
    private val mViewModel by viewModel<PokemonViewModel>()
    //通过hilt注入
//    private val mViewModel by lazy { ViewModelProvider(this).get(PokemonViewModel::class.java) }
    private val adapter: PokemonAdapter = PokemonAdapter {
        like(it)
    }

    private fun like(pokemonBean: PokemonBean) {
        pokemonBean.like = !pokemonBean.like
        adapter.notifyItemChanged(pokemonBean.id.toInt() - 1)
    }

    private val moshi by inject<Moshi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.getPokemon().observe(this, {
                lifecycleScope.launchWhenCreated {
                    adapter.submitData(it)
                }
            })
        with(binding) {
            motion = MotionPresenter()
            pokemon = mViewModel

            pokemonList.adapter = adapter.withLoadStateFooter(LoadMoreAdapter {
                adapter.retry()
            })
            pokemonList.layoutManager = LinearLayoutManager(this@MotionActivity)
            pokemonList.itemAnimator?.changeDuration = 0

            lifecycleScope.launchWhenCreated {
                adapter.addLoadStateListener {
                    mViewModel.refreshListener(it)
                }
            }

            /*pokemonRefresh.setOnRefreshListener {
                adapter.refresh()
            }*/
        }

//        binding.back.setOnClickListener { finish() }
    }

    inner class MotionPresenter {
        fun back() {
            finish()
        }

        fun refresh() {
            adapter.refresh()
        }
    }
}