package com.qtk.kotlintest.extensions

import android.content.Context
import androidx.core.content.ContextCompat
import org.jetbrains.anko.ctx

fun Context.color(res : Int) : Int = ContextCompat.getColor(this, res)