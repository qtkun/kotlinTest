package com.qtk.kotlintest.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.FragmentTextOneBinding
import com.qtk.kotlintest.extensions.singleClick
import com.qtk.kotlintest.extensions.viewBinding

class TestOneFragment: Fragment() {
    private val viewBinding by viewBinding<FragmentTextOneBinding>()

    private var pageNum = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = viewBinding.root

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