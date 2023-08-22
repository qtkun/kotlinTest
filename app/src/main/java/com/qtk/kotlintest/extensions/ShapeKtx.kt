package com.qtk.kotlintest.extensions

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel

fun View.roundedShape(
    @ColorInt fillColor: Int? = null,
    strokeWidth: Float = 0f,
    @ColorInt strokeColor: Int? = null
) {
    val shapeModel = ShapeAppearanceModel.Builder()
        .setAllCorners(RoundedCornerTreatment())
        .setAllCornerSizes(RelativeCornerSize(0.5f))
        .build()
    background = MaterialShapeDrawable(shapeModel).apply {
        this.fillColor = fillColor?.let { ColorStateList.valueOf(it) }
        this.strokeWidth = strokeWidth
        this.strokeColor = strokeColor?.let { ColorStateList.valueOf(it) }
    }
}

fun View.allCornerShape(
    cornerSize: Int = 0,
    @ColorInt normalColor: Int = Color.WHITE,
    @ColorInt pressColor: Int? = null,
    @ColorInt disableColor: Int? = null,
    strokeWidth: Float = 0f,
    @ColorInt strokeColor: Int? = null
) {
    background = MaterialShapeDrawable().apply {
        setCornerSize(cornerSize.dp.toFloat())
        val states = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf(-android.R.attr.state_enabled), intArrayOf())
        val colors = intArrayOf(pressColor ?: normalColor, disableColor ?: normalColor, normalColor)
        this.fillColor = ColorStateList(states, colors)
        this.strokeWidth = strokeWidth
        this.strokeColor = strokeColor?.let { ColorStateList.valueOf(it) }
    }
}

fun View.cornerShape(
    topLeftCornerSize: Int = 0,
    topRightCornerSize: Int = 0,
    bottomLeftCornerSize: Int = 0,
    bottomRightCornerSize: Int = 0,
    @ColorInt fillColor: Int? = null,
    strokeWidth: Float = 0f,
    @ColorInt strokeColor: Int? = null
) {
    val shapeModel = ShapeAppearanceModel.Builder()
        .setAllCorners(RoundedCornerTreatment())
        .setTopLeftCornerSize(topLeftCornerSize.dp.toFloat())
        .setTopRightCornerSize(topRightCornerSize.dp.toFloat())
        .setBottomLeftCornerSize(bottomLeftCornerSize.dp.toFloat())
        .setBottomRightCornerSize(bottomRightCornerSize.dp.toFloat())
        .build()
    background = MaterialShapeDrawable(shapeModel).apply {
        this.fillColor = fillColor?.let { ColorStateList.valueOf(it) }
        this.strokeWidth = strokeWidth
        this.strokeColor = strokeColor?.let { ColorStateList.valueOf(it) }
    }
}

@SuppressLint("RestrictedApi")
fun View.shadowCornerShape (
    cornerSize: Int = 0,
    @ColorInt normalColor: Int = Color.WHITE,
    @ColorInt pressColor: Int? = null,
    @ColorInt shadowColor: Int = Color.WHITE,
    shadowRadius: Int = 2,
    shadowVerticalOffset: Int = 2
) {
    (parent as? ViewGroup)?.clipChildren = false
    background = MaterialShapeDrawable().apply {
        paintStyle = Paint.Style.FILL
        setCornerSize(cornerSize.dp.toFloat())
        val states = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf())
        val colors = intArrayOf(pressColor ?: normalColor, normalColor)
        this.fillColor = ColorStateList(states, colors)
        this.shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
        this.elevation = shadowRadius.dp.toFloat()
        initializeElevationOverlay(context)
        this.shadowVerticalOffset = shadowVerticalOffset.dp
        setShadowColor(shadowColor)
    }
}

/*
@SuppressLint("RestrictedApi")
fun View.shadowCornerShape (
    topLeftCornerSize: Int = 0,
    topRightCornerSize: Int = 0,
    bottomLeftCornerSize: Int = 0,
    bottomRightCornerSize: Int = 0,
    @ColorInt fillColor: Int? = null,
    @ColorInt shadowColor: Int = Color.parseColor("#D2D2D2"),
    shadowRadius: Int = 0,
    shadowVerticalOffset: Int = 2.dp
) {
    val shapeModel = ShapeAppearanceModel.Builder()
        .setAllCorners(RoundedCornerTreatment())
        .setTopLeftCornerSize(topLeftCornerSize.dp.toFloat())
        .setTopRightCornerSize(topRightCornerSize.dp.toFloat())
        .setBottomLeftCornerSize(bottomLeftCornerSize.dp.toFloat())
        .setBottomRightCornerSize(bottomRightCornerSize.dp.toFloat())
        .build()
    (parent as? ViewGroup)?.clipChildren = false
    background = MaterialShapeDrawable(shapeModel).apply {
        this.fillColor = fillColor?.let { ColorStateList.valueOf(it) }
        this.shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
        this.elevation = shadowRadius.dp.toFloat()
        this.shadowVerticalOffset = shadowVerticalOffset
        setShadowColor(shadowColor)
    }
}*/
