package com.suenara.customvectordrawable.internal.elementparser

import android.content.res.XmlResourceParser
import com.suenara.customvectordrawable.internal.element.GroupElement

internal class GroupElementParser : ElementParser<GroupElement>() {
    override fun read(parser: XmlResourceParser): GroupElement {
        val name = readNullableStringValue(parser, GroupAttribute.Name)
        val pivotX = readFloatValue(parser, GroupAttribute.PivotX)
        val pivotY = readFloatValue(parser, GroupAttribute.PivotY)
        val rotation = readFloatValue(parser, GroupAttribute.Rotation)
        val scaleX = readFloatValue(parser, GroupAttribute.ScaleX)
        val scaleY = readFloatValue(parser, GroupAttribute.ScaleY)
        val translateX = readFloatValue(parser, GroupAttribute.TranslateX)
        val translateY = readFloatValue(parser, GroupAttribute.TranslateY)
        return GroupElement(
            name = name,
            pivotX = pivotX,
            pivotY = pivotY,
            rotation = rotation,
            scaleX = scaleX,
            scaleY = scaleY,
            translateX = translateX,
            translateY = translateY,
        )
    }

    private sealed class GroupAttribute<T>(override val tag: String, override val defaultValue: T) : Attribute<T> {
        object Name : GroupAttribute<String?>("name", null)
        object PivotX : GroupAttribute<Float>("pivotX", 0f)
        object PivotY : GroupAttribute<Float>("pivotY", 0f)
        object Rotation : GroupAttribute<Float>("rotation", 0f)
        object ScaleX : GroupAttribute<Float>("scaleX", 1f)
        object ScaleY : GroupAttribute<Float>("scaleY", 1f)
        object TranslateX : GroupAttribute<Float>("translateX", 0f)
        object TranslateY : GroupAttribute<Float>("translateY", 0f)
    }
}