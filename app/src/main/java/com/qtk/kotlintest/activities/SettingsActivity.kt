package com.qtk.kotlintest.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.VideoDecoder
import com.bumptech.glide.request.RequestOptions
import com.qtk.kotlintest.App
import com.qtk.kotlintest.adapter.CalendarPagerAdapter
import com.qtk.kotlintest.contant.DEFAULT_ZIP
import com.qtk.kotlintest.contant.ZIP_CODE
import com.qtk.kotlintest.databinding.ActivitySettingsBinding
import com.qtk.kotlintest.databinding.PopLayoutBinding
import com.qtk.kotlintest.extensions.*
import com.qtk.kotlintest.utils.dateToPosition
import com.qtk.kotlintest.widget.showBottom
import com.qtk.kotlintest.widget.showLeft
import com.qtk.kotlintest.widget.showRight
import com.qtk.kotlintest.widget.showTop
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.jetbrains.anko.toast
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread


class SettingsActivity : AppCompatActivity() {
    var zipCode: Long by DelegatesExt.preference(this, ZIP_CODE, DEFAULT_ZIP)
    private lateinit var popupWindow: PopupWindow
    private val popBinding: PopLayoutBinding by lazy { PopLayoutBinding.inflate(layoutInflater) }

    private val binding by inflate<ActivitySettingsBinding>()
    private val etState = MutableStateFlow(0L)

    private val permission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
            it[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
            getGifFirstFrame(
                this,"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fwww.99shcs.com%2Fuploads%2Fimg1%2F20210525%2Fc226c08c8b82980c5b529205d2ca832b.jpg&refer=http%3A%2F%2Fwww.99shcs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1648784459&t=2a63feed2182d9e0ebcd91965567ce10"
                )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        lifecycleScope.launchWhenCreated {
            App.instance.dataStore.getData(ZIP_CODE, DEFAULT_ZIP).collectLatest {
                binding.cityCode.setText(it.toString())
            }
        }
        intent.getStringExtra("toast")?.let { toast(it) }
        popupWindow = PopupWindow(this).apply {
            this.contentView = popBinding.root
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isOutsideTouchable = true
            isFocusable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        with(popBinding) {
            bingxiang.setOnClickListener {
                toast("冰箱")
                popupWindow.dismiss()
            }

            xiyiji.setOnClickListener {
                toast("洗衣机")
                popupWindow.dismiss()
            }

            caidian.setOnClickListener {
                toast("彩电")
                popupWindow.dismiss()
            }
        }
        with(binding) {
            heart1.setOnClickListener {
                popBinding.tc.showTop(popupWindow, it)
            }
            heart2.setOnClickListener {
                popBinding.tc.showBottom(popupWindow, it)
            }
            heart3.setOnClickListener {
                popBinding.tc.showLeft(popupWindow, it)
            }
            heart4.setOnClickListener {
                popBinding.tc.showRight(popupWindow, it)
            }
            /*cityCode.addTextChangedListener {
                etState.value = (it ?: "").toString()
            }*/
            PagerSnapHelper().attachToRecyclerView(calendarPager)
            val calendarPagerAdapter = CalendarPagerAdapter(calendarPager)
            calendarPager.adapter = calendarPagerAdapter
            calendarPager.layoutManager = LinearLayoutManager(this@SettingsActivity)
            calendarPager.scrollToPosition(dateToPosition())
            calendarPager.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val firstIndex =
                        (calendarPager.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    calendarPagerAdapter.months[firstIndex]?.let {
                        for (day in it) {
                            if (day.type == 1) {
                                date.text = "${day.year}年${day.month}月"
                                break
                            }
                        }
                    }
                }
            })
            cityCode.limitDecimal(5, 1)
            button.setOnLongClickListener {
                etState.value += 1L
                true
            }
        }
        permission.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))
        /*AnimatorInflater.loadAnimator(this, R.animator.scale).apply {
            setTarget(binding.heart1)
            start()
        }*/
        lifecycleScope.launchWhenResumed {
            etState
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .sample(100L)
                .filter {
                    it != 0L
                }
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    cityCode.setText(it.toString())
                    if (binding.button.isPressed) {
                        etState.value += 1L
                    }
//                    dataStore.putData(ZIP_CODE, it.toLong())
                }
        }
    }

    fun getGifFirstFrame(context: Context, url: String?) {
        thread {
            try {

                val drawable = Glide.with(context).asGif().load(url).submit().get()

                val newFile = File("${getExternalFilesDir("cache")}/first_frame.png")
                if(newFile.exists()){
                    newFile.delete()
                    newFile.createNewFile()
                }
                BufferedOutputStream(FileOutputStream(newFile)).use {
                    drawable.firstFrame.compress(Bitmap.CompressFormat.PNG, 60, it)
                    it.flush()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed(); true
        }
        else -> false
    }

    override fun onBackPressed() {
        lifecycleScope.launchWhenResumed {
            App.instance.dataStore.putData(ZIP_CODE, binding.cityCode.text.toString().toLong())
            zipCode = binding.cityCode.text.toString().toLong()
            val intent = Intent()
            intent.putExtra("toast", "我从原生页面回来了")
            setResult(Activity.RESULT_OK, intent)
            super.onBackPressed()
        }
    }
}