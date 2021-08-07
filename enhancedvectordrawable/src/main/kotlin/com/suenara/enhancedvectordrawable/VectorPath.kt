package com.suenara.enhancedvectordrawable

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.Keep

/**
 * E.g how to change color animation of some path that has name "shape_1" in xml definition:
 *    val evd = EnhancedVectorDrawable(context, R.drawable.my_drawable)
 *    evd.findPath("shape_1")?.run {
 *        strokeColor = Color.MAGENTA
 *        evd.invalidateSelf()
 *    }
 */
@Keep
interface VectorPath : AnimationTarget {
    @get:ColorInt
    @setparam:ColorInt
    var fillColor: Int

    @get:ColorInt
    @setparam:ColorInt
    var strokeColor: Int

    var strokeWidth: Float

    @setparam:IntRange(from = 0, to = 255)
    @get:IntRange(from = 0, to = 255)
    var strokeAlpha: Int
}