package com.suenara.customvectordrawable

import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.Color
import android.util.AttributeSet
import java.lang.NumberFormatException

internal fun floatAlphaToInt(alpha: Float): Int = minOf(255, (255 * alpha).toInt())


class CachedParser(val parser: XmlResourceParser) : XmlResourceParser by parser {
    private val attributes = mutableMapOf<String, Map<String, Int>>()

    override fun next(): Int {
        val next = parser.next()
        attributes[parser.name] = parser.attributeIndices()
        return next
    }

    fun attributes(): Map<String, Int> = attributes[parser.name].orEmpty()
    fun attributes(tag: String): Map<String, Int> = attributes[tag].orEmpty()
}

fun AttributeSet.attributeIndices(): Map<String, Int> {
    return mutableMapOf<String, Int>().also { map ->
            for (idx in 0 until attributeCount) map[getAttributeName(idx)] = idx
        }
}

fun String.parseColorInt(): Int {
    return when (length) {
        2 -> {
            Color.parseColor(buildString {
                append('#')
                repeat(8) { append(get(1)) }
            })
        }
        4 -> {
            Color.parseColor(buildString {
                append('#')
                append(get(1)); append(get(1)); append(get(2)); append(get(2)); append(get(3)); append(get(3))
            })
        }
        7 -> Color.parseColor(this)
        9 -> Color.parseColor(this)
        else -> Color.TRANSPARENT
    }
}

fun dimensionValue(context: Context, value: String): Float {
    return try {
        DimensionConverter.stringToDimension(value, context)
    } catch (_: NumberFormatException) {
        value.toFloat()
    }
}