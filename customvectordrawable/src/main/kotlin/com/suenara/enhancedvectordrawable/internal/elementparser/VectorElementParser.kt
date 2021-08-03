package com.suenara.enhancedvectordrawable.internal.elementparser

import android.content.res.XmlResourceParser
import com.suenara.enhancedvectordrawable.internal.element.Shape
import com.suenara.enhancedvectordrawable.internal.floatAlphaToInt

internal class VectorElementParser : ElementParser<Shape>() {
    override fun read(parser: XmlResourceParser): Shape {
        val viewportWidth: Float = readFloatValue(parser, VectorAttribute.ViewportWidth)
        val viewportHeight: Float = readFloatValue(parser, VectorAttribute.ViewportHeight)
        val alpha: Int = floatAlphaToInt(readFloatValue(parser, VectorAttribute.Alpha))
        val name: String? = readNullableStringValue(parser, VectorAttribute.Name)
        val width: Float = readDimensionFloatValue(parser, VectorAttribute.Width)
        val height: Float = readDimensionFloatValue(parser, VectorAttribute.Height)

        return Shape(
            name = name,
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
            alpha = alpha,
            width = width,
            height = height
        )
    }

    private sealed class VectorAttribute<T>(override val tag: String, override val defaultValue: T) : Attribute<T> {
        object ViewportWidth : VectorAttribute<Float>("viewportWidth", 0f)
        object ViewportHeight : VectorAttribute<Float>("viewportHeight", 0f)
        object Alpha : VectorAttribute<Float>("alpha", 1f)
        object Name : VectorAttribute<String?>("name", null)
        object Width : VectorAttribute<Float>("width", 0f)
        object Height : VectorAttribute<Float>("height", 0f)
    }
}