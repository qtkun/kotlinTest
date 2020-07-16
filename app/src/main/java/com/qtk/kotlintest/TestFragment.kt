package com.qtk.kotlintest

import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TestFragment : Fragment() {
    val viewModel by sharedViewModel<MainViewModel> ()
}