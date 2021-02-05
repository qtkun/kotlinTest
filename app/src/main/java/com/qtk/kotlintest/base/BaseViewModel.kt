package com.qtk.kotlintest.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    protected val mutableLoading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = mutableLoading
}