package com.suenara.enhancedvectordrawable.internal.animatorparser

internal sealed class AnimatorValue<T>() {
    abstract val value: T

    data class FloatNumber(override val value: Float) : AnimatorValue<Float>()
    data class IntNumber(override val value: Int) : AnimatorValue<Int>()
    data class Path(override val value: String) : AnimatorValue<String>()
    data class Color(override val value: Int) : AnimatorValue<Int>()
    object Undefined : AnimatorValue<Nothing>() {
        override val value: Nothing get() = throw IllegalStateException()
        override fun toString(): String = "Undefined"
    }
}