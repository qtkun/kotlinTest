package com.qtk.kotlintest.activities

import com.qtk.kotlintest.adapter.PictureAdapterProxy
import com.qtk.kotlintest.base.base.BaseActivity
import com.qtk.kotlintest.base.base.BaseViewModel
import com.qtk.kotlintest.base.base.MultiAdapter
import com.qtk.kotlintest.databinding.ActivityPictureBinding


class PictureActivity: BaseActivity<ActivityPictureBinding, BaseViewModel>()/*, OnTouchListener */{
    companion object {
        private const val TAG = "PictureActivity"
        const val PICTURE_URL = "PICTURE_URL"
    }

    override fun lightMode(): Boolean = false

    override fun ActivityPictureBinding.initViewBinding() {
        val urls = intent.getStringArrayListExtra(PICTURE_URL)
        pictureVp.adapter = MultiAdapter(arrayListOf(PictureAdapterProxy()), mutableListOf<Any>().apply {
            urls?.let { addAll(it) }
        })
        pictureVp.offscreenPageLimit = 4
    }

    override fun BaseViewModel.initViewModel() {}
}