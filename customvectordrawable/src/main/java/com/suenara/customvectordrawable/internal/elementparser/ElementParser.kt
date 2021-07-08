package com.suenara.customvectordrawable.internal.elementparser

import android.content.res.XmlResourceParser
import org.xmlpull.v1.XmlPullParser

internal abstract class ElementParser<T> {

    abstract fun read(parser: XmlResourceParser): T

    protected fun getAttrPosition(xpp: XmlPullParser, attrName: String): Int {
        for (i in 0 until xpp.attributeCount) {
            if (xpp.getAttributeName(i).equals(attrName)) {
                return i;
            }
        }
        return -1;
    }

    protected fun readFloatValue(parser: XmlResourceParser, attr: Attribute<Float>): Float =
        read(parser, attr) { it.toFloat() }

    protected fun readDimensionFloatValue(parser: XmlResourceParser, attr: Attribute<Float>): Float =
        read(parser, attr) { it.dimenStringToFloat() }

    protected fun readNullableStringValue(parser: XmlResourceParser, attr: Attribute<String?>): String? =
        read(parser, attr) { it }

    protected inline fun <E> read(
        parser: XmlResourceParser,
        attr: Attribute<E>,
        valueMapper: (String) -> E
    ): E {
        return getAttrPosition(parser, attr.tag).let { pos ->
            if (pos.isValid) valueMapper(parser.getAttributeValue(pos)) else attr.defaultValue
        }
    }

    private fun String.dimenStringToFloat(): Float {
        val suffixLength = if (endsWith("dip")) 3 else 2
        return substring(0, length - suffixLength).toFloat()
    }

    protected val Int.isValid: Boolean
        get() = this != -1

    protected interface Attribute<T> {
        val tag: String
        val defaultValue: T
    }
}