package com.qtk.kotlintest.activities

import android.app.Activity
import android.view.View
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.App
import com.qtk.kotlintest.R
import com.qtk.kotlintest.extensions.ctx
import com.qtk.kotlintest.extensions.slideEnter
import com.qtk.kotlintest.extensions.slideExit
import com.qtk.kotlintest.method.IntentMethod
import io.flutter.embedding.android.FlutterActivity
import com.qtk.kotlintest.activities.FlutterActivity as FA
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
                    /*toolbar.ctx.startActivity(FlutterActivity
                        .withCachedEngine("test")
                        .build(toolbar.ctx))*/
                    activity.startActivityForResult<FA>(
                        IntentMethod.RequestCode,
                        "engine" to "test"
                    )
                }
                R.id.action_flutter2 -> {
                    activity.startActivityForResult<FA>(
                        IntentMethod.RequestCode,
                        "engine" to "test2"
                    )
                }
                R.id.action_flutter3 -> {
                    activity.startActivityForResult<FA>(
                        IntentMethod.RequestCode,
                        "engine" to "test3"
                    )
                }
                R.id.action_goods -> toolbar.ctx.startActivity<MotionActivity>()
                R.id.action_viewpager -> toolbar.ctx.startActivity<ViewPagerActivity>()
                R.id.action_camera -> toolbar.ctx.startActivity<CameraActivity>()
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