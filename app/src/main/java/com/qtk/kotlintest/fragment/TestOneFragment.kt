package com.qtk.kotlintest.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.FragmentTextOneBinding
import com.qtk.kotlintest.extensions.bindView
import com.qtk.kotlintest.extensions.singleClick

class TestOneFragment: Fragment(R.layout.fragment_text_one) {
    private val viewBinding by bindView<FragmentTextOneBinding>()

    private var pageNum = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt("PAGE_NUM")?.let {
            pageNum = it + 1
        }
        viewBinding.text.text = "One $pageNum"
        viewBinding.back.singleClick {
            findNavController().navigateUp()
        }
        viewBinding.button.singleClick {
            findNavController().navigate(R.id.action_testOneFragment_self, Bundle().apply {
                putInt("PAGE_NUM", pageNum)
            })
        }
    }
}