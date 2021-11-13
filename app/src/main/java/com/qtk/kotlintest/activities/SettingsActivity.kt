package com.qtk.kotlintest.activities

import android.animation.AnimatorInflater
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.qtk.kotlintest.App
import com.qtk.kotlintest.adapter.CalendarPagerAdapter
import com.qtk.kotlintest.contant.DEFAULT_ZIP
import com.qtk.kotlintest.contant.ZIP_CODE
import com.qtk.kotlintest.databinding.ActivitySettingsBinding
import com.qtk.kotlintest.databinding.PopLayoutBinding
import com.qtk.kotlintest.extensions.*
import com.qtk.kotlintest.utils.*
import com.qtk.kotlintest.widget.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity() {
    var zipCode : Long by DelegatesExt.preference(this, ZIP_CODE, DEFAULT_ZIP)
    private lateinit var popupWindow: PopupWindow
    private val popBinding: PopLayoutBinding by lazy { PopLayoutBinding.inflate(layoutInflater) }

    private val binding by inflate<ActivitySettingsBinding>()
    private val etState = MutableStateFlow(0L)

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
                    val firstIndex = (calendarPager.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
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
            button.setOnLongClickListener {
                etState.value += 1L
                true
            }
        }
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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { onBackPressed(); true }
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