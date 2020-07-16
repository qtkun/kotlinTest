package com.qtk.kotlintest.test

import androidx.fragment.app.Fragment
import com.qtk.kotlintest.view_model.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TestFragment : Fragment() {
    val viewModel by sharedViewModel<MainViewModel> ()
}