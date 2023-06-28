package com.qtk.kotlintest.activities

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.qtk.kotlintest.R
import com.qtk.kotlintest.adapter.VideoAdapterProxy
import com.qtk.kotlintest.base.base.BaseActivity
import com.qtk.kotlintest.base.base.MultiAdapter
import com.qtk.kotlintest.base.base.MultiTypeListAdapter
import com.qtk.kotlintest.databinding.ActivityVideoListBinding
import com.qtk.kotlintest.utils.onScrollPlayVideo
import com.qtk.kotlintest.utils.onScrollReleaseAllVideos
import com.qtk.kotlintest.view_model.VideoListViewModel
import com.qtk.kotlintest.widget.SpringEdgeEffect
import com.shuyu.gsyvideoplayer.GSYVideoManager

class VideoListActivity: BaseActivity<ActivityVideoListBinding, VideoListViewModel>(), ToolbarManager {
    override val toolbar by lazy { binding.toolbar.toolbar }
    override val activity: Activity by lazy { this }

    override fun ActivityVideoListBinding.initViewBinding() {
        initToolbar()
        videoRv.apply {
            adapter = MultiTypeListAdapter(mutableListOf(VideoAdapterProxy())).apply {
                submitList(viewModel.videoList)
            }
            layoutManager = LinearLayoutManager(this@VideoListActivity)
            edgeEffectFactory = SpringEdgeEffect()
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        onScrollPlayVideo(recyclerView, R.id.detail_player)
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy == 0) {
                        onScrollPlayVideo(recyclerView, R.id.detail_player)
                    } else {
                        onScrollReleaseAllVideos(recyclerView, VideoAdapterProxy.TAG, 0.2f)
                    }
                }
            })

            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }

    override fun VideoListViewModel.initViewModel() {

    }

    override fun onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        GSYVideoManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        GSYVideoManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

}