package com.qtk.kotlintest.extensions

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@MainThread
inline fun<reified VB: ViewBinding> Activity.viewBinding(setContentView: Boolean = true) =
    ViewBindingLazy(VB::class.java, { layoutInflater }) { if (setContentView) setContentView(it.root) }
@MainThread
inline fun <reified VB : ViewBinding> Dialog.viewBinding(setContentView: Boolean = true) =
    ViewBindingLazy(VB::class.java, { layoutInflater }) { if (setContentView) setContentView(it.root) }

@MainThread
inline fun <reified VB : ViewBinding> Fragment.viewBinding() = ViewBindingLazy(VB::class.java, { layoutInflater })

@MainThread
inline fun <reified VB: ViewBinding> ViewGroup.viewBinding(attachToRoot: Boolean = false) =
    ViewBindingLazy(VB::class.java, { LayoutInflater.from(context) }, { this }, attachToRoot)

@MainThread
inline fun <reified VB: ViewBinding> ViewGroup.mergeViewBinding() =
    MergeViewBindingLazy(VB::class.java, { LayoutInflater.from(context) }, { this })

@Suppress("UNCHECKED_CAST")
class ViewBindingLazy<VB: ViewBinding>(
    private val viewBindingClass: Class<VB>,
    private val layoutInflater: () -> LayoutInflater,
    private val root: (() -> ViewGroup)? = null,
    private val attachToRoot: Boolean = false,
    private val setContentView: ((VB) -> Unit)? = null
): Lazy<VB> {
    private var cache: VB? = null
    override val value: VB
        get() {
            return cache ?:
            (viewBindingClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
                .invoke(null, layoutInflater.invoke(), root?.invoke(), attachToRoot) as VB).also {
                    setContentView?.invoke(it)
                    cache = it
                }
        }

    override fun isInitialized(): Boolean = cache != null
}

class MergeViewBindingLazy<VB: ViewBinding>(
    private val viewBindingClass: Class<VB>,
    private val layoutInflater: () -> LayoutInflater,
    private val root: (() -> ViewGroup)? = null
): Lazy<VB> {
    private var cache: VB? = null
    override val value: VB
        get() {
            return cache ?:
            (viewBindingClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java)
                .invoke(null, layoutInflater.invoke(), root?.invoke()) as VB).also { cache = it }
        }

    override fun isInitialized(): Boolean = cache != null
}

@MainThread
inline fun <reified VB : ViewBinding> bindItemView(view: View) = lazy(LazyThreadSafetyMode.NONE) {
    VB::class.java.getMethod("bind",View::class.java).invoke(null, view) as VB
}

@MainThread
inline fun <reified VB : ViewBinding> bindView() = FragmentBindingDelegate(VB::class.java)

@Suppress("UNCHECKED_CAST")
class FragmentBindingDelegate<VB : ViewBinding>(
    private val clazz: Class<VB>
) : ReadOnlyProperty<Fragment, VB> {

    private var isInitialized = false
    private var _binding: VB? = null
    private val binding: VB get() = _binding!!

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (!isInitialized) {
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    _binding = null
                    isInitialized = false
                }
            })
            _binding = clazz.getMethod("bind", View::class.java)
                .invoke(null, thisRef.requireView()) as VB
            isInitialized = true
        }
        return binding
    }
}

