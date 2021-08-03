package com.suenara.enhancedvectordrawable

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.annotation.ColorInt
import androidx.annotation.IntRange

fun EnhancedVectorDrawable.setStrokeColor(pathName: String, @ColorInt color: Int) {
    findPath(pathName)?.run {
        strokeColor = color
        invalidateSelf()
    }
}

fun EnhancedVectorDrawable.setFillColor(pathName: String, @ColorInt color: Int) {
    findPath(pathName)?.run {
        fillColor = color
        invalidateSelf()
    }
}

fun EnhancedVectorDrawable.setColor(pathName: String, @ColorInt color: Int) {
    findPath(pathName)?.run {
        fillColor = color
        strokeColor = color
        invalidateSelf()
    }
}


fun EnhancedVectorDrawable.setStrokeAlpha(pathName: String, @IntRange(from = 0, to = 255) alpha: Int) {
    findPath(pathName)?.run {
        strokeAlpha = alpha
        invalidateSelf()
    }
}

fun EnhancedVectorDrawable.setStrokeWidth(pathName: String, strokeWidth: Float) {
    findPath(pathName)?.run {
        this.strokeWidth = strokeWidth
        invalidateSelf()
    }
}

inline fun VectorAnimationContainer.changeAnimations(targetName: String, crossinline action: (Animator) -> Unit) {
    findAnimations(targetName)?.let {
        action(it)
        invalidateAnimations()
    }
}

fun VectorAnimationContainer.changeAnimations(
    targetName: String,
    property: AnimationTarget.Property,
    vararg values: Any
) = changeAnimations(targetName) { animator ->
    (animator as? AnimatorSet)?.childAnimations
        ?.find { it is ObjectAnimator }
        ?.let { it as ObjectAnimator }
        ?.let { objAnim ->
            objAnim.values.forEach { pvh ->
                if (pvh.propertyName == property.tag) {
                    property.setValues(pvh, *values)
                }
            }
        }
}