package com.suenara.customvectordrawable.internal.elementparser

import android.content.res.XmlResourceParser
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.suenara.customvectordrawable.internal.element.PathElement
import com.suenara.customvectordrawable.internal.floatAlphaToInt
import com.suenara.customvectordrawable.internal.parseColorInt

internal open class PathElementParser : ElementParser<PathElement>() {

    override fun read(parser: XmlResourceParser): PathElement {
        val name = readNullableStringValue(parser, PathAttribute.Name)
        val fillAlpha = floatAlphaToInt(readFloatValue(parser, PathAttribute.FillAlpha))
        val fillColor = readColor(parser, PathAttribute.FillColor)
        val fillType = readFillType(parser, PathAttribute.FillType)
        val pathData = readNullableStringValue(parser, PathAttribute.PathData)
        val strokeAlpha = floatAlphaToInt(readFloatValue(parser, PathAttribute.StrokeAlpha))
        val strokeColor = readColor(parser, PathAttribute.StrokeColor)
        val strokeLineCap = readLineCap(parser, PathAttribute.StrokeLineCap)
        val strokeLineJoin = readLineJoin(parser, PathAttribute.StrokeLineJoin)
        val strokeMiterLimit = readFloatValue(parser, PathAttribute.StrokeMiterLimit)
        val strokeWidth = readFloatValue(parser, PathAttribute.StrokeWidth)
        val trimPathEnd = readFloatValue(parser, PathAttribute.TrimPathEnd)
        val trimPathOffset = readFloatValue(parser, PathAttribute.TrimPathOffset)
        val trimPathStart = readFloatValue(parser, PathAttribute.TrimPathStart)
        return PathElement(
            name = name,
            fillAlpha = fillAlpha,
            fillColor = fillColor,
            fillType = fillType,
            pathData = pathData,
            strokeAlpha = strokeAlpha,
            strokeColor = strokeColor,
            strokeLineCap = strokeLineCap,
            strokeLineJoin = strokeLineJoin,
            strokeMiterLimit = strokeMiterLimit,
            strokeWidth = strokeWidth,
            trimPathEnd = trimPathEnd,
            trimPathOffset = trimPathOffset,
            trimPathStart = trimPathStart
        )
    }

    private fun readColor(parser: XmlResourceParser, attr: Attribute<Int>): Int =
        read(parser, attr) { it.parseColorInt() }

    private fun readFillType(parser: XmlResourceParser, attr: Attribute<Path.FillType>): Path.FillType =
        read(parser, attr) { it.parseFillType() }

    private fun readLineCap(parser: XmlResourceParser, attr: Attribute<Paint.Cap>): Paint.Cap =
        read(parser, attr) { it.parseLineCap() }

    private fun readLineJoin(parser: XmlResourceParser, attr: Attribute<Paint.Join>): Paint.Join =
        read(parser, attr) { it.parseLineJoin() }

    private fun String.parseFillType(): Path.FillType = when (this) {
        "1" -> Path.FillType.EVEN_ODD
        "2" -> Path.FillType.INVERSE_WINDING
        "3" -> Path.FillType.INVERSE_EVEN_ODD
        else -> Path.FillType.WINDING
    }

    private fun String.parseLineCap(): Paint.Cap = when (this) {
        "1" -> Paint.Cap.ROUND
        "2" -> Paint.Cap.SQUARE
        else -> Paint.Cap.BUTT
    }


    private fun String.parseLineJoin(): Paint.Join = when (this) {
        "1" -> Paint.Join.ROUND
        "2" -> Paint.Join.BEVEL
        else -> Paint.Join.MITER
    }

    private sealed class PathAttribute<T>(override val tag: String, override val defaultValue: T) : Attribute<T> {
        object Name : PathAttribute<String?>("name", null)
        object FillAlpha : PathAttribute<Float>("fillAlpha", 1f)
        object FillColor : PathAttribute<Int>("fillColor", Color.TRANSPARENT)
        object FillType : PathAttribute<Path.FillType>("fillType", Path.FillType.WINDING)
        object PathData : PathAttribute<String?>("pathData", null)
        object StrokeAlpha : PathAttribute<Float>("strokeAlpha", 1f)
        object StrokeColor : PathAttribute<Int>("strokeColor", Color.TRANSPARENT)
        object StrokeLineCap : PathAttribute<Paint.Cap>("strokeLineCap", Paint.Cap.BUTT)
        object StrokeLineJoin : PathAttribute<Paint.Join>("strokeLineJoin", Paint.Join.MITER)
        object StrokeMiterLimit : PathAttribute<Float>("strokeMiterLimit", 4f)
        object StrokeWidth : PathAttribute<Float>("strokeWidth", 0f)
        object TrimPathEnd : PathAttribute<Float>("trimPathEnd", 1f)
        object TrimPathOffset : PathAttribute<Float>("trimPathOffset", 0f)
        object TrimPathStart : PathAttribute<Float>("trimPathStart", 0f)
    }
}