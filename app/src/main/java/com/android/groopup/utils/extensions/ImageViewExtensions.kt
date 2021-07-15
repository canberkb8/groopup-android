package com.android.groopup.utils.extensions

import android.content.Context
import android.graphics.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.databinding.BindingAdapter
import com.android.groopup.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.transition.Transition
import java.lang.StrictMath.min

/**
 * Load model into ImageView as a circle image with borderSize (optional) using Glide
 *
 * @param model - Any object supported by Glide (Uri, File, Bitmap, String, resource id as Int, ByteArray, and Drawable)
 * @param borderInDip - The border size in density-independent pixels
 * @param borderColor - The border color
 */
fun <T> ImageView.loadCircularImage(
    model: T, borderInDip: Float = 0F, borderColor: Int = R.color.light_navy
) {
    Glide.with(context)
        .asBitmap()
        .load(model)
        .apply(RequestOptions.circleCropTransform())
        .into(object : BitmapImageViewTarget(this) {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                setImageDrawable(resource.run {
                    RoundedBitmapDrawableFactory.create(
                        resources, if (context.convertDpToPixel(borderInDip) > 0) {
                            createBitmapWithBorder(
                                context.convertDpToPixel(borderInDip),
                                borderColor,
                                context
                            )
                        } else {
                            this
                        }
                    ).apply {
                        isCircular = true
                    }
                })
            }
        })
}

private fun Context.convertDpToPixel(borderInDip: Float): Float {
    return (16.0f * borderInDip + 0f)
}

/**
 * Create a new bordered bitmap with the specified borderSize and borderColor
 *
 * @param borderSize - The border size in pixels
 * @param borderColor - The border color
 * @param context - Android context for resolving the color resource
 * @return A new bordered bitmap with the specified borderSize and borderColor
 */
fun Bitmap.createBitmapWithBorder(
    borderSize: Float,
    borderColor: Int = R.color.light_navy,
    context: Context
): Bitmap {
    val borderOffset = (borderSize * 2).toInt()
    val halfWidth = width / 2
    val halfHeight = height / 2
    val circleRadius = min(halfWidth, halfHeight).toFloat()
    val newBitmap = Bitmap.createBitmap(
        width + borderOffset, height + borderOffset, Bitmap.Config.ARGB_8888
    )

    // Center coordinates of the image
    val centerX = halfWidth + borderSize
    val centerY = halfHeight + borderSize

    val paint = Paint()
    val canvas = Canvas(newBitmap).apply {
        // Set transparent initial area
        drawARGB(0, 0, 0, 0)
    }

    // Draw the transparent initial area
    paint.isAntiAlias = true
    paint.style = Paint.Style.FILL
    canvas.drawCircle(centerX, centerY, circleRadius, paint)

    // Draw the image
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, borderSize, borderSize, paint)

    // Draw the createBitmapWithBorder
    paint.xfermode = null
    paint.style = Paint.Style.STROKE
    paint.color = ContextCompat.getColor(context, borderColor)
    paint.strokeWidth = borderSize
    canvas.drawCircle(centerX, centerY, circleRadius, paint)
    return newBitmap
}


// -- USAGE -- //

@BindingAdapter(
    "glideCircleUrl",
    "glideCircleBorderSize",
    "glideCircleBorderColor",
    requireAll = false
)
fun withGlideOrEmpty(
    imageView: ImageView, url: String = "", borderSize: Float = 0F, borderColor: Int = R.color.light_navy
) {
    imageView.loadCircularImage(url, borderSize, borderColor)
}

// -- XML EXAMPLE -- //
// bind:glideCircleBorderColor="@{R.color.dark_blue}"
// bind:glideCircleBorderSize="@{2}"
// bind:glideCircleUrl="@{viewModel.domainObject.imageUrl}"