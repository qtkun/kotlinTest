package com.qtk.kotlintest.base

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.LoadingDialogBinding
import com.qtk.kotlintest.extensions.toPx
import dagger.hilt.android.AndroidEntryPoint
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseActivity<VDB : ViewDataBinding>(@LayoutRes val contentLayoutId: Int) : AppCompatActivity() {
    lateinit var binding: VDB
    private val dialogBinding: LoadingDialogBinding by lazy {
        LoadingDialogBinding.inflate(layoutInflater)
    }
    private val dialog: Dialog by lazy {
        Dialog(this).apply {
            setContentView(dialogBinding.root)
            setCanceledOnTouchOutside(false)
            window?.also {
                it.setBackgroundDrawableResource(R.drawable.loading_bg)
                it.setGravity(Gravity.CENTER)
                it.setLayout(150.0.toPx(), 100.0.toPx())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, contentLayoutId)
        binding.lifecycleOwner = this
    }

    fun showLoading(tips: String = "正在加载...") {
        dialogBinding.tips.text = tips
        dialog.show()
    }

    fun hideLoading() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}

inline fun <reified VM: BaseViewModel> BaseActivity<*>.initLoading(lifecycleOwner: LifecycleOwner, vm: VM){
    vm.loading.observe(lifecycleOwner) {
        if (it) {
            showLoading()
        } else {
            hideLoading()
        }
    }
}