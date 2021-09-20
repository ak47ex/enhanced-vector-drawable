package com.suenara.enhancedvectordrawable

import android.animation.PropertyValuesHolder
import androidx.annotation.Keep

@Keep
interface AnimationTarget {

    @Suppress("UNCHECKED_CAST", "unused")
    enum class Property(
        val tag: String,
        private val valueSetter: PropertyValuesHolder.(Array<out Any>) -> Unit
    ) {
        FILL_COLOR("fillColor", {
            setIntValues(*(it as Array<Int>).toIntArray())
        }),
        STROKE_COLOR("strokeColor", {
            setIntValues(*(it as Array<Int>).toIntArray())
        }),
        STROKE_WIDTH("strokeWidth", {
            setFloatValues(*(it as Array<Float>).toFloatArray())
        }),
        STROKE_ALPHA("strokeAlpha", {
            setIntValues(*(it as Array<Int>).toIntArray())
        });

        fun setValues(pvh: PropertyValuesHolder, vararg values: Any) = valueSetter(pvh, values)
    }
}