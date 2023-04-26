package com.qtk.kotlintest.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import com.qtk.kotlintest.R
import com.qtk.kotlintest.databinding.ActivityNavTestBinding
import com.qtk.kotlintest.extensions.inflate
import com.qtk.kotlintest.extensions.singleClick
import com.qtk.kotlintest.fragment.NavMainOneFragment
import com.qtk.kotlintest.fragment.NavMainTwoFragment

class NavTestActivity: AppCompatActivity() {
    private val viewBinding by inflate<ActivityNavTestBinding>()


    private var oneFragment: NavMainOneFragment? = null
    private var twoFragment: NavMainTwoFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_test)
        with(viewBinding) {
            one.singleClick {
                switchPage(0)
            }
            two.singleClick {
                switchPage(1)
            }
        }
        switchPage(0)
    }

    private fun switchPage(page: Int) {
        val trans = supportFragmentManager.beginTransaction()
        when(page) {
            0 -> {
                if (twoFragment != null) {
                    trans.hide(twoFragment!!)
                }
                if (oneFragment == null) {
                    oneFragment = NavMainOneFragment()
                    trans.add(R.id.nav_host_fragment, oneFragment!!)
                } else {
                    trans.show(oneFragment!!)
                }
                trans.commitAllowingStateLoss()
            }
            1 -> {
                if (oneFragment != null) {
                    trans.hide(oneFragment!!)
                }
                if (twoFragment == null) {
                    twoFragment = NavMainTwoFragment()
                    trans.add(R.id.nav_host_fragment, twoFragment!!)
                } else {
                    trans.show(twoFragment!!)
                }
                trans.commitAllowingStateLoss()
            }
        }
    }

    /*override fun onStart() {
        super.onStart()
        controller = findNavController(R.id.nav_host_fragment)
        controller.setGraph(navOne, null)
    }*/

    /*override fun onSupportNavigateUp(): Boolean {
        return controller.navigateUp()
    }

    override fun onBackPressed() {
        controller.let {
            val count = it.backQueue.count { entry ->
                entry.destination !is NavGraph
            }
            if (count == 1) {
                super.onBackPressed()
            } else {
                it.navigateUp()
            }
        }
    }*/
}