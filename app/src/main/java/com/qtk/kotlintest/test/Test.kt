package com.qtk.kotlintest.test

class Solution {
    fun runningSum(nums: IntArray): IntArray {
        for(i in 1 until nums.size) {
            nums[i] += nums[i - 1]
        }
        return nums
    }
}

data class TestBean(
    val selected: Boolean
) {
    val unselected1 get() = !selected
    val unselected = !selected
}