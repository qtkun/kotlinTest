package com.qtk.kotlintest.base

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.qtk.kotlintest.BR
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.LoadingDialogBinding
import com.qtk.kotlintest.extensions.toPx
import java.util.Stack

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

    private val resultCallBacks = Stack<(Intent) -> Unit>()
    private val activityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result?.let {
            if (it.resultCode == RESULT_OK) {
                it.data?.let { data -> resultCallBacks.pop()?.invoke(data) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, contentLayoutId)
        binding.lifecycleOwner = this
    }

    protected fun startActivityResult(
        cls: Class<*>,
        block: Intent.() -> Unit = {},
        callback: (Intent) -> Unit
    ){
        resultCallBacks.push(callback)
        activityForResult.launch(Intent(this, cls).apply(block))
    }

    protected fun finishForResult(
        block: Intent.() -> Unit = {}
    ) {
        setResult(RESULT_OK, Intent().apply(block))
        finish()
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
    binding.setVariable(BR.viewModel, vm)
    vm.loading.observe(lifecycleOwner) {
        if (it) {
            showLoading()
        } else {
            hideLoading()
        }
    }
}