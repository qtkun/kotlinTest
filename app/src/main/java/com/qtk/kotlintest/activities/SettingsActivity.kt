package com.qtk.kotlintest.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.italic
import androidx.core.text.scale
import androidx.core.text.superscript
import androidx.core.text.toHtml
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.qtk.flowbus.observe.observeEvent
import com.qtk.kotlintest.App
import com.qtk.kotlintest.calender.getToday
import com.qtk.kotlintest.calender.initDateBottomSheet
import com.qtk.kotlintest.contant.DEFAULT_ZIP
import com.qtk.kotlintest.contant.ZIP_CODE
import com.qtk.kotlintest.databinding.ActivitySettingsBinding
import com.qtk.kotlintest.databinding.PopLayoutBinding
import com.qtk.kotlintest.extensions.DelegatesExt
import com.qtk.kotlintest.extensions.allCornerShape
import com.qtk.kotlintest.extensions.getData
import com.qtk.kotlintest.extensions.launchAndCollectIn
import com.qtk.kotlintest.extensions.limitDecimal
import com.qtk.kotlintest.extensions.putData
import com.qtk.kotlintest.extensions.shadowCornerShape
import com.qtk.kotlintest.extensions.singleClick
import com.qtk.kotlintest.extensions.toast
import com.qtk.kotlintest.extensions.viewBinding
import com.qtk.kotlintest.widget.showBottom
import com.qtk.kotlintest.widget.showLeft
import com.qtk.kotlintest.widget.showRight
import com.qtk.kotlintest.widget.showTop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.sample
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread


class SettingsActivity : AppCompatActivity() {
    var zipCode: Long by DelegatesExt.preference(this, ZIP_CODE, DEFAULT_ZIP)
    private lateinit var popupWindow: PopupWindow
    private val popBinding: PopLayoutBinding by lazy { PopLayoutBinding.inflate(layoutInflater) }

    private val binding by viewBinding<ActivitySettingsBinding>()
    private val etState = MutableStateFlow(0L)

    private val permission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
//        checkPermissionResult()
    }
    private var progress = 0f

    private val date = MutableStateFlow(getToday())

    private val datePickerDialog by lazy {
        initDateBottomSheet(
            "${date.value} 00:00:00" ,
            onConfirm = {
                date.value = it
            },
            true
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setSupportActionBar(binding.toolbar.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
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
            setOnDismissListener {
                window?.apply {
                    attributes = attributes.apply {
                        alpha = 1f
                    }
                }
            }
        }
        with(binding) {
            val spanStr = buildSpannedString {
                color(Color.RED) {
                    scale(1.2f) {
                        bold {
                            append("City")
                        }
                    }
                }
                color(Color.GREEN) {
                    scale(1.5f) {
                        italic {
                            append(" Code")
                        }
                    }
                }
                scale(0.5f) {
                    superscript {
                        append("TM")
                    }
                }
            }
            clTest.allCornerShape(
                26,
                Color.parseColor("#34D092"),
                Color.parseColor("#179664"),
                Color.parseColor("#DFE2E4"),
            )
            clTest.setOnTouchListener { v, event ->
                when(event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        test.shadowCornerShape(
                            26,
                            shadowColor = Color.parseColor("#179664"),
                            shadowRadius = 46, shadowVerticalOffset = 14
                        )
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        test.shadowCornerShape(
                            26,
                            shadowColor = Color.parseColor("#34D092"),
                            shadowRadius = 46, shadowVerticalOffset = 14
                        )
                    }
                }
                false
            }
            test.shadowCornerShape(
                26,
                shadowColor = Color.parseColor("#34D092"),
                shadowRadius = 46, shadowVerticalOffset = 14
            )
            clTest.setOnClickListener {  }

            Log.i("qtkun", spanStr.toHtml())
            cityTitle.text = spanStr

            cityTitle.singleClick {
                popBinding.tc.showTop(popupWindow, it)
            }
            heart1.setOnClickListener {
                popBinding.tc.showLeft(popupWindow, it)
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

            tvDay.singleClick {
                datePickerDialog.show()
            }

            cityCode.limitDecimal(5, 1)
            button.setOnLongClickListener {
                etState.value += 1L
                true
            }
            button.setOnClickListener {
                if (progress == 100f) {
                    progress = 0f
                    progressBar.setProgress(progress, false)
                } else {
                    progress += 10f
                    progressBar.setProgress(progress)
                }
            }
        }
        permission.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))
        /*AnimatorInflater.loadAnimator(this, R.animator.scale).apply {
            setTarget(binding.heart1)
            start()
        }*/
        observeEvent<String>("platform", true) {
            toast(it)
        }
        etState
            .sample(100L)
            .filter {
                it != 0L
            }
            .distinctUntilChanged { old, new ->
                old == new
            }
            .flowOn(Dispatchers.IO)
            .launchAndCollectIn(this, Lifecycle.State.STARTED) {
                binding.cityCode.setText(it.toString())
                if (binding.button.isPressed) {
                    etState.value += 1L
                }
            }
        date.launchAndCollectIn(this, Lifecycle.State.RESUMED) {
            binding.tvDay.text = it
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

    /*private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permission.launch(
                arrayOf(READ_MEDIA_IMAGES,
                    READ_MEDIA_VIDEO,
                    READ_MEDIA_VISUAL_USER_SELECTED)
            )
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            permission.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            permission.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }
    }*/

    /*private fun checkPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, READ_MEDIA_VIDEO) == PERMISSION_GRANTED)
        ) {
            // Android 13及以上完整照片和视频访问权限
        } else if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            ContextCompat.checkSelfPermission(this, READ_MEDIA_VISUAL_USER_SELECTED) == PERMISSION_GRANTED
        ) {
            // Android 14及以上部分照片和视频访问权限
        } else if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            // Android 12及以下完整本地读写访问权限
        } else {
            // 无本地读写访问权限
        }
    }*/

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed(); true
        }
        else -> false
    }

    override fun onBackPressed() {
        Log.i("qtk", "onBackPressed")
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