package com.suenara.customvectordrawable

internal fun floatAlphaToInt(alpha: Float): Int = minOf(255, (255 * alpha).toInt())