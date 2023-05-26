package com.qtk.kotlintest.activities

import android.app.Activity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.App
import com.qtk.kotlintest.R
import com.qtk.kotlintest.extensions.ctx
import com.qtk.kotlintest.extensions.slideEnter
import com.qtk.kotlintest.extensions.slideExit
//import com.qtk.kotlintest.method.IntentMethod
//import com.qtk.kotlintest.activities.FlutterActivity as FA
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

interface ToolbarManager {
    val toolbar : Toolbar

    val activity : Activity

    var toolbarTitle : String
        get() = toolbar.title.toString()
        set(value) {
            toolbar.title = value
        }

    fun initToolbar(){
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_settings -> toolbar.ctx.startActivity<SettingsActivity>()
                R.id.action_flutter -> {
                   /* activity.startActivityForResult<FA>(
                        IntentMethod.RequestCode,
                        "engine" to "test"
                    )*/
                    toolbar.ctx.startActivity<FlowActivity>()
                }
                R.id.action_flutter2 -> {
                    /*activity.startActivityForResult<FA>(
                        IntentMethod.RequestCode,
                        "engine" to "test2"
                    )*/
                    toolbar.ctx.startActivity<PictureActivity>(
                        PictureActivity.PICTURE_URL to mutableListOf(
                            "https://img0.baidu.com/it/u=2289446283,2987162055&fm=253&fmt=auto&app=120&f=JPEG?w=1422&h=800",
                            "https://img0.baidu.com/it/u=3295304401,3564425098&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=3000",
                            "https://img0.baidu.com/it/u=242767209,2541342896&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
                            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F201702%2F05%2F20170205213628_dj3ic.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1686128761&t=ca4b8cdbb0c21a3cdaf7426d0180b860",
                            "https://img0.baidu.com/it/u=3957758939,1600769248&fm=253&fmt=auto&app=120&f=JPEG?w=1280&h=800"
                        )
                    )
                }
                R.id.action_flutter3 -> {
                    /*activity.startActivityForResult<FA>(
                        IntentMethod.RequestCode,
                        "engine" to "test3"
                    )*/
                }
                R.id.action_pokemon -> toolbar.ctx.startActivity<MotionActivity>()
                R.id.action_viewpager -> toolbar.ctx.startActivity<ViewPagerActivity>()
                R.id.action_camera -> toolbar.ctx.startActivity<CameraActivity>()
                R.id.action_map -> toolbar.ctx.startActivity<MapActivity>()
                R.id.action_video -> toolbar.ctx.startActivity<VideoListActivity>()
                else -> App.instance.toast("Unknown option")
            }
            true
        }
    }

    fun enableHomeAsUp(up: () -> Unit) {
        toolbar.navigationIcon = createUpDrawable()
        toolbar.setNavigationOnClickListener { up() }
    }

    private fun createUpDrawable() = DrawerArrowDrawable(toolbar.ctx).apply { progress = 1f }

    fun attachToScroll(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) toolbar.slideExit() else toolbar.slideEnter()
            }
        })
    }
}