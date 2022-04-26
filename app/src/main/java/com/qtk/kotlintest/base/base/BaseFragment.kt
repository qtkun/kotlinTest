package com.qtk.kotlintest.base.base

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.LoadingDialogBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType
import java.util.*

abstract class BaseFragment<VB: ViewBinding, VM: BaseViewModel>: Fragment() {
    lateinit var binding: VB
    lateinit var viewModel: VM

    private val dialogBinding: LoadingDialogBinding by lazy {
        LoadingDialogBinding.inflate(layoutInflater).apply {
            progressCircular.indeterminateDrawable.setTint(Color.WHITE)
        }
    }
    private val dialog by lazy {
        context?.let { ctx ->
            Dialog(ctx).apply {
                setContentView(dialogBinding.root)
                setCanceledOnTouchOutside(false)
                window?.also {
                    it.setBackgroundDrawableResource(R.drawable.loading_bg)
                    it.setGravity(Gravity.CENTER)
                    it.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                }
            }
        }
    }

    private val resultCallBacks = Stack<(Intent) -> Unit>()
    private val activityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result?.let {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                it.data?.let { data -> resultCallBacks.pop()?.invoke(data) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (javaClass.genericSuperclass as ParameterizedType).let {
            it.actualTypeArguments.let { types ->
                binding = (types[0] as Class<VB>).getMethod("inflate", LayoutInflater::class.java,
                    ViewGroup::class.java, Boolean::class.java).invoke(null, layoutInflater, container, false) as VB
                viewModel = (types[1] as Class<VM>).run { ViewModelProvider(this@BaseFragment)[this] }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.loading.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect {
                if (it) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }
        }
        initVariables(savedInstanceState)
    }

    protected fun startActivityForResult(
        cls: Class<*>,
        block: Intent.() -> Unit = {},
        callback: (Intent) -> Unit
    ){
        activity?.run {
            resultCallBacks.push(callback)
            activityForResult.launch(Intent(this, cls).apply(block))
        }
    }

    protected fun finishForResult(
        block: Intent.() -> Unit = {}
    ) {
        activity?.run {
            setResult(AppCompatActivity.RESULT_OK, Intent().apply(block))
            finish()
        }
    }

    protected abstract fun VB.initViewBinding()

    protected abstract fun VM.initViewModel()

    protected open fun initVariables(savedInstanceState: Bundle?) {
        binding.initViewBinding()
        viewModel.initViewModel()
    }

    private fun showLoading(tips: String = "加载中...") {
        dialogBinding.tips.text = tips
        dialog?.show()
    }

    private fun hideLoading() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }
}