package com.suenara.customvectordrawable.internal.animatorparser

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.Color
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import com.suenara.customvectordrawable.internal.attributeIndices
import com.suenara.customvectordrawable.internal.dimensionValue
import com.suenara.customvectordrawable.internal.parseColorInt

internal sealed class AnimatorAttribute<T>(private val tag: String, private val defaultValue: T) {

    abstract fun convert(context: Context, parser: XmlResourceParser, index: Int): T

    fun get(context: Context, parser: XmlResourceParser): T {
        val index = parser.attributeIndices()[tag] ?: return defaultValue
        return convert(context, parser, index)
    }

    object Interpolator :
        AnimatorAttribute<android.view.animation.Interpolator>("interpolator", LinearInterpolator()) {
        override fun convert(
            context: Context,
            parser: XmlResourceParser,
            index: Int
        ): android.view.animation.Interpolator {
            val interpolatorRes = parser.getAttributeResourceValue(index, 0)
            return if (interpolatorRes != 0) {
                AnimationUtils.loadInterpolator(context, interpolatorRes)
            } else {
                throw IllegalStateException("Can't parse interpolator")
            }
        }
    }

    object Duration : AnimatorAttribute<Long>("duration", 300L) {
        override fun convert(context: Context, parser: XmlResourceParser, index: Int): Long =
            parser.getAttributeValue(index).toLongOrNull() ?: 0L
    }

    object ValueFrom : AnimatorAttribute<AnimatorValue<*>?>("valueFrom", null) {
        override fun convert(context: Context, parser: XmlResourceParser, index: Int): AnimatorValue<*> {
            return when (ValueType.get(context, parser)) {
                is AnimatorValue.Color -> AnimatorValue.Color(parser.getAttributeValue(index).parseColorInt())
                is AnimatorValue.FloatNumber -> AnimatorValue.FloatNumber(parser.floatAt(index, context))
                is AnimatorValue.IntNumber -> AnimatorValue.IntNumber(parser.intAt(index))
                is AnimatorValue.Path -> AnimatorValue.Path(parser.getAttributeValue(index))
                AnimatorValue.Undefined -> TODO()
                null -> throw IllegalStateException()
            }
        }
    }

    object ValueTo : AnimatorAttribute<AnimatorValue<*>?>("valueTo", null) {
        override fun convert(context: Context, parser: XmlResourceParser, index: Int): AnimatorValue<*> {
            return when (ValueType.get(context, parser)) {
                is AnimatorValue.Color -> AnimatorValue.Color(parser.getAttributeValue(index).parseColorInt())
                is AnimatorValue.FloatNumber -> AnimatorValue.FloatNumber(parser.floatAt(index, context))
                is AnimatorValue.IntNumber -> AnimatorValue.IntNumber(parser.intAt(index))
                is AnimatorValue.Path -> AnimatorValue.Path(parser.getAttributeValue(index))
                AnimatorValue.Undefined -> TODO()
                null -> throw IllegalStateException()
            }
        }
    }

    object ValueType : AnimatorAttribute<AnimatorValue<*>>("valueType", AnimatorValue.IntNumber(0)) {
        override fun convert(context: Context, parser: XmlResourceParser, index: Int): AnimatorValue<*> {
            return when (parser.intAt(index)) {
                0 -> AnimatorValue.FloatNumber(0f)
                1 -> AnimatorValue.IntNumber(0)
                2 -> AnimatorValue.Path("")
                3 -> AnimatorValue.Color(Color.MAGENTA)
                4 -> AnimatorValue.Undefined
                else -> throw IllegalStateException("unknown value type ${parser.getAttributeValue(index)}")
            }
        }
    }

    object PropertyName : AnimatorAttribute<String>("propertyName", "") {
        override fun convert(context: Context, parser: XmlResourceParser, index: Int): String =
            parser.getAttributeValue(index)
    }

    object PropertyXName : AnimatorAttribute<String>("propertyXName", "") {
        override fun convert(context: Context, parser: XmlResourceParser, index: Int): String =
            parser.getAttributeValue(index)
    }

    object PropertyYName : AnimatorAttribute<String>("propertyYName", "") {
        override fun convert(context: Context, parser: XmlResourceParser, index: Int): String =
            parser.getAttributeValue(index)
    }

    object PathData : AnimatorAttribute<String>("pathData", "") {
        override fun convert(context: Context, parser: XmlResourceParser, index: Int): String =
            parser.getAttributeValue(index)
    }


    object StartDelay : AnimatorAttribute<Long>("startOffset", 0) {
        override fun convert(context: Context, parser: XmlResourceParser, index: Int): Long = parser.longAt(index)
    }

    object RepeatCount : AnimatorAttribute<Int>("repeatCount", 0) {
        override fun convert(context: Context, parser: XmlResourceParser, index: Int): Int = parser.intAt(index)
    }

    object RepeatMode : AnimatorAttribute<Int>("repeatMode", ValueAnimator.RESTART) {
        override fun convert(context: Context, parser: XmlResourceParser, index: Int): Int = parser.intAt(index)
    }

    protected fun XmlResourceParser.intAt(index: Int): Int = getAttributeValue(index).toInt()
    protected fun XmlResourceParser.longAt(index: Int): Long = getAttributeValue(index).toLong()
    protected fun XmlResourceParser.floatAt(index: Int, context: Context): Float =
        dimensionValue(context, getAttributeValue(index))
}