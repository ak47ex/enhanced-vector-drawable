package com.suenara.customvectordrawable

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.Keep

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