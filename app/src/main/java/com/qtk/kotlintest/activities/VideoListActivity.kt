package com.qtk.kotlintest.activities

import android.widget.ImageView
import com.qtk.kotlintest.adapter.VideoListAdapterProxy
import com.qtk.kotlintest.base.base.BaseActivity
import com.qtk.kotlintest.base.base.BaseViewHolder
import com.qtk.kotlintest.base.base.MultiAdapter
import com.qtk.kotlintest.databinding.ActivityVideoListBinding
import com.qtk.kotlintest.databinding.ItemListVideoBinding
import com.qtk.kotlintest.preload.PreloadManager
import com.qtk.kotlintest.view_model.VideoListViewModel
import com.qtk.kotlintest.widget.EmptyControlVideo
import com.qtk.kotlintest.widget.OnViewPagerListener
import com.qtk.kotlintest.widget.ViewPagerLayoutManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper

class VideoListActivity: BaseActivity<ActivityVideoListBinding, VideoListViewModel>() {

    val adapter by lazy { MultiAdapter(mutableListOf(VideoListAdapterProxy()), viewModel.videoList) }
    private val viewPagerLayoutManager by lazy {
        ViewPagerLayoutManager(this).apply {
            offscreenPageLimit = 3
            setOnViewPagerListener(object: OnViewPagerListener {
                override fun onInitComplete() {
                    playCurVideo(0)
                }

                override fun onPageRelease(isNext: Boolean, position: Int) {

                }

                override fun onPageSelected(position: Int, isBottom: Boolean) {
                    playCurVideo(position)
                }

                override fun onPreloadResume(position: Int, isReverseScroll: Boolean) {
                    PreloadManager.instance.resumePreload(position, isReverseScroll)
                }

                override fun onPreloadPause(position: Int, isReverseScroll: Boolean) {
                    PreloadManager.instance.pausePreload(position, isReverseScroll)
                }

            })
        }
    }

    private lateinit var smallVideoHelper: GSYVideoHelper
    private lateinit var gsySmallVideoHelperBuilder: GSYVideoHelper.GSYVideoHelperBuilder

    override fun ActivityVideoListBinding.initViewBinding() {
        smallVideoHelper = GSYVideoHelper(this@VideoListActivity, EmptyControlVideo(this@VideoListActivity))

        gsySmallVideoHelperBuilder = GSYVideoHelper.GSYVideoHelperBuilder()
        gsySmallVideoHelperBuilder.setHideStatusBar(true)
            .setHideActionBar(true)
            .setNeedLockFull(true)
            .setCacheWithPlay(true)
            .setShowFullAnimation(false)
            .setRotateViewAuto(false)
            .setLockLand(true)
            .setLooping(true)
            .setIsTouchWiget(false)
        smallVideoHelper.setGsyVideoOptionBuilder(gsySmallVideoHelperBuilder)
        videoRv.apply {
            adapter = this@VideoListActivity.adapter
            layoutManager = viewPagerLayoutManager
        }
        /*videoRv.apply {
            adapter = MultiAdapter(mutableListOf(VideoAdapterProxy()), viewModel.videoList)
            layoutManager = LinearLayoutManager(this@VideoListActivity)
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
        }*/
    }

    override fun VideoListViewModel.initViewModel() {

    }

    private fun playCurVideo(position: Int){
        if (position == smallVideoHelper.playPosition) return
        (binding.videoRv.findViewHolderForLayoutPosition(position) as? BaseViewHolder<ItemListVideoBinding>)?.let { holder ->
            smallVideoHelper.setPlayPositionAndTag(position, VideoListAdapterProxy.TAG)
            smallVideoHelper.addVideoPlayer(position, ImageView(this), VideoListAdapterProxy.TAG, holder.binding.flContainer, holder.binding.playBtn)
            gsySmallVideoHelperBuilder.url = adapter.getItem(position) as? String
            smallVideoHelper.startPlay()
        }

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
        smallVideoHelper.releaseVideoPlayer()
        GSYVideoManager.releaseAllVideos()
        PreloadManager.instance.removeAllPreloadTask()
    }

}