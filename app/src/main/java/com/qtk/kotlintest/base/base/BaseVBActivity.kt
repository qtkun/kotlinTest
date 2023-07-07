package com.qtk.kotlintest.base.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.zackratos.ultimatebarx.ultimatebarx.statusBarOnly
import java.lang.reflect.ParameterizedType
import java.util.Stack

abstract class BaseVBActivity<VB: ViewBinding>: AppCompatActivity() {
    lateinit var binding: VB

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
        (javaClass.genericSuperclass as ParameterizedType).let {
            it.actualTypeArguments.let { types ->
                binding = (types[0] as Class<VB>).getMethod("inflate", LayoutInflater::class.java,
                    ViewGroup::class.java, Boolean::class.java).invoke(null, layoutInflater, null, false) as VB
                setContentView(binding.root)
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

    protected fun startActivityForResult(
        intent: Intent,
        block: Intent.() -> Unit = {},
        callback: (Intent) -> Unit
    ){
        resultCallBacks.push(callback)
        activityForResult.launch(intent.apply(block))
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

    protected open fun initVariables(savedInstanceState: Bundle?) {
        binding.initViewBinding()
    }
}
