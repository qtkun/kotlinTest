package com.qtk.kotlintest.extensions

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun<reified VB: ViewBinding> Activity.inflate() = lazy(LazyThreadSafetyMode.NONE) {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

inline fun <reified VB : ViewBinding> Dialog.inflate() = lazy(LazyThreadSafetyMode.NONE) {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

inline fun <reified VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater) =
    VB::class.java.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB


inline fun <reified VB : ViewBinding> bindView() = FragmentBindingDelegate(VB::class.java)

inline fun <reified VB : ViewBinding> bindItemView(view: View) = lazy(LazyThreadSafetyMode.NONE) {
    VB::class.java.getMethod("bind",View::class.java).invoke(null, view) as VB
}

@Suppress("UNCHECKED_CAST")
class FragmentBindingDelegate<VB : ViewBinding>(
    private val clazz: Class<VB>
) : ReadOnlyProperty<Fragment, VB> {

    private var isInitialized = false
    private var _binding: VB? = null
    private val binding: VB get() = _binding!!

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (!isInitialized) {
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroyView() {
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

