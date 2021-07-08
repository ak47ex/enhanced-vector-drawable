package com.suenara.customvectordrawable

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import java.util.regex.Matcher
import java.util.regex.Pattern


object DimensionConverter {
    // -- Initialize dimension string to constant lookup.
    private val dimensionConstantLookup: Map<String, Int> = HashMap<String, Int>().also { m ->
        m["px"] = TypedValue.COMPLEX_UNIT_PX
        m["dip"] = TypedValue.COMPLEX_UNIT_DIP
        m["dp"] = TypedValue.COMPLEX_UNIT_DIP
        m["sp"] = TypedValue.COMPLEX_UNIT_SP
        m["pt"] = TypedValue.COMPLEX_UNIT_PT
        m["in"] = TypedValue.COMPLEX_UNIT_IN
        m["mm"] = TypedValue.COMPLEX_UNIT_MM
    }

    // -- Initialize pattern for dimension string.
    private val DIMENSION_PATTERN: Pattern = Pattern.compile("^\\s*(\\d+(\\.\\d+)*)\\s*([a-zA-Z]+)\\s*$")


    fun stringToDimensionPixelSize(dimension: String, context: Context): Int =
        stringToDimensionPixelSize(dimension, context.resources.displayMetrics)

    fun stringToDimensionPixelSize(dimension: String, metrics: DisplayMetrics): Int {
        // -- Mimics TypedValue.complexToDimensionPixelSize(int data, DisplayMetrics metrics).
        val internalDimension = stringToInternalDimension(dimension)
        val value = internalDimension.value
        val f = TypedValue.applyDimension(internalDimension.unit, value, metrics)
        val res = (f + 0.5f).toInt()
        if (res != 0) return res
        if (value == 0f) return 0
        return if (value > 0) 1 else -1
    }

    fun stringToDimension(dimension: String, context: Context): Float =
        stringToDimension(dimension, context.resources.displayMetrics)

    fun stringToDimension(dimension: String, metrics: DisplayMetrics): Float {
        // -- Mimics TypedValue.complexToDimension(int data, DisplayMetrics metrics).
        val internalDimension = stringToInternalDimension(dimension)
        return TypedValue.applyDimension(internalDimension.unit, internalDimension.value, metrics)
    }

    private fun stringToInternalDimension(dimension: String): InternalDimension {
        // -- Match target against pattern.
        val matcher: Matcher = DIMENSION_PATTERN.matcher(dimension)
        return if (matcher.matches()) {
            // -- Match found.
            // -- Extract value.
            val value: Float = java.lang.Float.valueOf(matcher.group(1)).toFloat()
            // -- Extract dimension units.
            val unit: String = matcher.group(3).toLowerCase()
            // -- Get Android dimension constant.
            val dimensionUnit = dimensionConstantLookup[unit]
            dimensionUnit?.let {
                // -- Return valid dimension.
                InternalDimension(value, it)
            } ?: // -- Invalid format.
            throw NumberFormatException()
        } else {
            // -- Invalid format.
            throw NumberFormatException()
        }
    }

    private data class InternalDimension(val value: Float, val unit: Int)
}