package com.qtk.kotlintest.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.LoadMoreAdapter
import com.qtk.kotlintest.adapter.PokemonAdapter
import com.qtk.kotlintest.base.BaseActivity
import com.qtk.kotlintest.databinding.ActivityMotionBinding
import com.qtk.kotlintest.databinding.ItemPokemonBinding
import com.qtk.kotlintest.retrofit.data.PokemonBean
import com.qtk.kotlintest.view_model.PokemonViewModel
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@AndroidEntryPoint
class MotionActivity : BaseActivity<ActivityMotionBinding>(R.layout.activity_motion) {
    //通过koin注入
//    private val mViewModel by viewModel<PokemonViewModel>()
    //通过hilt注入
    private val mViewModel by lazy { ViewModelProvider(this)[PokemonViewModel::class.java] }
    private val adapter: PokemonAdapter = PokemonAdapter {pokemon, binding ->
        like(pokemon, binding)
    }

    private fun like(pokemonBean: PokemonBean,binding: ItemPokemonBinding) {
        pokemonBean.like = !pokemonBean.like
        adapter.notifyItemChanged(pokemonBean.id.toInt() - 1)
        AnimatorSet().apply {
            val scalex = ObjectAnimator.ofFloat(binding.like,"ScaleX", 1f, 0.6f)
            val scaley = ObjectAnimator.ofFloat(binding.like,"ScaleY", 1f, 0.6f)
            val scalex2 = ObjectAnimator.ofFloat(binding.like,"ScaleX", 0.6f, 1f)
            val scaley2 = ObjectAnimator.ofFloat(binding.like,"ScaleY", 0.6f, 1f)
            play(scalex).with(scaley)
            play(scalex2).after(scalex)
            play(scaley2).after(scaley)
            start()
        }
    }

    private val moshi by inject<Moshi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.getPokemon().observe(this) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    adapter.submitData(it)
                }
            }
        }
        with(binding) {
            motion = MotionPresenter()
            pokemon = mViewModel

            LinearSnapHelper().attachToRecyclerView(pokemonList)
            pokemonList.adapter = adapter.withLoadStateFooter(LoadMoreAdapter {
                adapter.retry()
            })
            pokemonList.layoutManager = LinearLayoutManager(this@MotionActivity)
            pokemonList.itemAnimator = null
            /*pokemonList.addItemDecoration(
                StickyDecoration (backgroundColor = this@MotionActivity.color(R.color.colorAccent)) {
                    return@StickyDecoration "${it / 3 + 1}"
                }
            )
            pokemonList.addItemDecoration(
                TimeLineDecoration(this@MotionActivity.color(R.color.colorAccent))
            )*/

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

        fun scroll() {
            startActivityResult(SettingsActivity::class.java) {
                toast(it.getStringExtra("test") ?: "")
            }
//            (binding.pokemonList.layoutManager as LinearLayoutManager).smoothScroll(this@MotionActivity, 10)
        }
    }
}