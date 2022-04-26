package com.qtk.kotlintest.base.base

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.LoadingDialogBinding
import com.zackratos.ultimatebarx.ultimatebarx.addStatusBarTopPadding
import com.zackratos.ultimatebarx.ultimatebarx.statusBarOnly
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType
import java.util.*

abstract class BaseActivity<VB: ViewBinding, VM: BaseViewModel>: AppCompatActivity() {
    lateinit var binding: VB
    lateinit var viewModel: VM

    private val dialogBinding: LoadingDialogBinding by lazy {
        LoadingDialogBinding.inflate(layoutInflater).apply {
            progressCircular.indeterminateDrawable.setTint(Color.WHITE)
        }
    }
    private val dialog: Dialog by lazy {
        Dialog(this).apply {
            setContentView(dialogBinding.root)
            setCanceledOnTouchOutside(false)
            window?.also {
                it.setBackgroundDrawableResource(R.drawable.loading_bg)
                it.setGravity(Gravity.CENTER)
                it.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
        val contentView = findViewById<ViewGroup>(android.R.id.content)
        (javaClass.genericSuperclass as ParameterizedType).let {
            it.actualTypeArguments.let { types ->
                binding = (types[0] as Class<VB>).getMethod("inflate", LayoutInflater::class.java,
                    ViewGroup::class.java, Boolean::class.java).invoke(null, layoutInflater, contentView, false) as VB
                setContentView(binding.root)
                viewModel = (types[1] as Class<VM>).run { ViewModelProvider(this@BaseActivity)[this] }
            }
        }
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
        statusBarOnly {
            transparent()
            light = lightMode()
        }
    }

    protected fun startActivityForResult(
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

    protected open fun lightMode(): Boolean {
        return true
    }

    protected abstract fun VB.initViewBinding()

    protected abstract fun VM.initViewModel()

    protected open fun initVariables(savedInstanceState: Bundle?) {
        findToolbar(binding.root)?.run {
            addStatusBarTopPadding()
            setNavigationOnClickListener {
                navigationListener()
            }
            setOnMenuItemClickListener { item ->
                menuItemListener(item)
                true
            }
        }
        binding.initViewBinding()
        viewModel.initViewModel()
    }

    protected open fun navigationListener() {
        finish()
    }

    protected open fun menuItemListener(menuItem: MenuItem) {}

    private fun findToolbar(view: View): Toolbar? {
        when (view) {
            is Toolbar -> {
                return view
            }
            is ViewGroup -> {
                for (v in view.children) {
                    return findToolbar(v)
                }
            }
        }
        return null
    }

    private fun showLoading(tips: String = "加载中...") {
        dialogBinding.tips.text = tips
        dialog.show()
    }

    private fun hideLoading() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}
