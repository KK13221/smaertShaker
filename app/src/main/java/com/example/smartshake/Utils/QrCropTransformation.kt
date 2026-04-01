package com.example.smartshake.Utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class QrCropTransformation : BitmapTransformation() {

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {

        val width = toTransform.width
        val height = toTransform.height

         /**
         * Based on your Razorpay template image:
         * QR approx center me hai
         * width ka ~65% square area QR hota hai
         */

        val side = (width * 0.6).toInt()

        val left = (width - side) / 2

        // QR approx 36% height se start hota hai
        var top = (height * 0.4).toInt()

        // Safety check (important)
        if (top + side > height) {
            top = height - side
        }

        val sourceRect = Rect(
            left,
            top,
            left + side,
            top + side
        )

        // Create cropped bitmap
        val result = pool.get(
            side,
            side,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(result)

        val destRect = Rect(
            0,
            0,
            side,
            side
        )

        canvas.drawBitmap(
            toTransform,
            sourceRect,
            destRect,
            null
        )

        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(
            "qr_crop_transformation_v2".toByteArray()
        )
    }
}