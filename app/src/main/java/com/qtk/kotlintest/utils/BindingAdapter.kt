package com.qtk.kotlintest.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.qtk.kotlintest.extensions.toDateString

@BindingAdapter("imageListUrl", "placeholder")
fun loadListImage(view: ImageView, url: String, drawable: Drawable) {
    Glide.with(view.context)
        .load(url)
        .placeholder(drawable)
        .transform(RoundedCorners(5))
        .override(300, 300)
        .into(view)
}

@BindingAdapter("imageUrl", "placeholder")
fun loadImage(view: ImageView, url: String, drawable: Drawable) {
    Glide.with(view.context)
        .load(url)
        .placeholder(drawable)
        .override(300, 300)
        .into(view)
}

@BindingConversion
fun dateToString(long: Long): String = long.toDateString()

@BindingAdapter("bindLoading")
fun bindingLoading(swipe: SwipeRefreshLayout, isLoading: Boolean) {
    swipe.isRefreshing = isLoading
}

@BindingAdapter("refreshListener")
fun bindRefreshListener(swipe: SwipeRefreshLayout, refresh: () -> Unit) {
    swipe.setOnRefreshListener {
        refresh()
    }
}

