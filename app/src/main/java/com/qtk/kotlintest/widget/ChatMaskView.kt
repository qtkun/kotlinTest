package com.qtk.kotlintest.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.util.AttributeSet

/**
 * 用S代表源像素，源像素的颜色值可表示为[Sa, Sc]，Sa中的a是alpha的缩写，Sa表示源像素的Alpha值，Sc中的c是颜色color的缩写，Sc表示源像素的RGB。
 * 用D代表目标像素，目标像素的颜色值可表示为[Da, Dc]，Da表示目标像素的Alpha值，Dc表示目标像素的RGB。
 *
 * 源像素与目标像素在不同混合模式下计算颜色的规则如下所示：
 *
 * CLEAR：[0, 0]
 *
 * SRC：[Sa, Sc]
 *
 * DST：[Da, Dc]
 *
 * SRC_OVER：[Sa + (1 - Sa)*Da, Rc = Sc + (1 - Sa)*Dc]
 *
 * DST_OVER：[Sa + (1 - Sa)*Da, Rc = Dc + (1 - Da)*Sc]
 *
 * SRC_IN：[Sa * Da, Sc * Da]
 *
 * DST_IN：[Sa * Da, Sa * Dc]
 *
 * SRC_OUT：[Sa * (1 - Da), Sc * (1 - Da)]
 *
 * DST_OUT：[Da * (1 - Sa), Dc * (1 - Sa)]
 *
 * SRC_ATOP：[Da, Sc * Da + (1 - Sa) * Dc]
 *
 * DST_ATOP：[Sa, Sa * Dc + Sc * (1 - Da)]
 *
 * XOR：[Sa + Da - 2 * Sa * Da, Sc * (1 - Da) + (1 - Sa) * Dc]
 *
 * DARKEN：[Sa + Da - Sa*Da, Sc*(1 - Da) + Dc*(1 - Sa) + min(Sc, Dc)]
 *
 * LIGHTEN：[Sa + Da - Sa*Da, Sc*(1 - Da) + Dc*(1 - Sa) + max(Sc, Dc)]
 *
 * MULTIPLY：[Sa * Da, Sc * Dc]
 *
 * SCREEN：[Sa + Da - Sa * Da, Sc + Dc - Sc * Dc]
 *
 * ADD：Saturate(S + D)
 *
 * OVERLAY：Saturate(S + D)
 *
 */
class ChatMaskView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
): androidx.appcompat.widget.AppCompatImageView(context, attributeSet, defStyleAttr) {
    private var mSrcBitmap: Bitmap? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)

    override fun onDraw(canvas: Canvas) {
        if (mSrcBitmap == null || mSrcBitmap?.isRecycled == true) {
            super.onDraw(canvas)
            return
        }
        val save = canvas.saveLayer(null, null)
        paint.shader = getShader()
        canvas.drawBitmap(mSrcBitmap!!, 0f, 0f, null)
        paint.xfermode = xfermode
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null
        paint.xfermode = null
        canvas.restoreToCount(save)
    }

    fun setBitmap(bitmap: Bitmap) {
        mSrcBitmap = bitmap
        postInvalidate()
    }

    private fun getShader():Shader {
        return LinearGradient(width / 2f, height.toFloat(), width / 2f, 0f,
            intArrayOf(Color.parseColor("#00FFFFFF"), Color.parseColor("#FFFFFFFF")), null, Shader.TileMode.CLAMP)
    }
}