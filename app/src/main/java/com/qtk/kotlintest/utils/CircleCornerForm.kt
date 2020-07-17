package com.qtk.kotlintest.utils

import android.graphics.*
import com.squareup.picasso.Transformation

class CircleCornerForm : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val widthLight: Int = source.width
        val heightLight: Int = source.height
        val output: Bitmap =
            Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paintColor = Paint()
        paintColor.flags = Paint.ANTI_ALIAS_FLAG
        val rectF = RectF(Rect(0, 0, widthLight, heightLight))
        canvas.drawRoundRect(rectF, (widthLight / 5).toFloat(),
            (heightLight / 5).toFloat(), paintColor)
        val paintImage = Paint()
        paintImage.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(source, 0f, 0f, paintImage)
        source.recycle()
        return output
    }

    override fun key(): String {
        return "roundcorner"
    }
}