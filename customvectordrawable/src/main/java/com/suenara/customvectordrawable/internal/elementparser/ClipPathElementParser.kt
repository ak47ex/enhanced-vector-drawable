package com.suenara.customvectordrawable.internal.elementparser

import android.content.res.XmlResourceParser
import com.suenara.customvectordrawable.internal.element.ClipPathElement

internal class ClipPathElementParser : ElementParser<ClipPathElement>() {
    override fun read(parser: XmlResourceParser): ClipPathElement {
        val name = readNullableStringValue(parser, ClipPathAttribute.Name)
        val pathData = readNullableStringValue(parser, ClipPathAttribute.PathData)
        return ClipPathElement(
            name = name,
            pathData = pathData
        )
    }

    private sealed class ClipPathAttribute<T>(override val tag: String, override val defaultValue: T) : Attribute<T> {
        object Name : ClipPathAttribute<String?>("name", null)
        object PathData : ClipPathAttribute<String?>("pathData", null)
    }
}