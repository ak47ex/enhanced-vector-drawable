package com.suenara.customvectordrawable.internal.element

import android.graphics.Matrix
import androidx.annotation.Keep
import com.suenara.customvectordrawable.AnimationTarget

@Keep
internal class GroupElement(
    val name: String?,
    pivotX: Float,
    pivotY: Float,
    rotation: Float,
    scaleX: Float,
    scaleY: Float,
    translateX: Float,
    translateY: Float,
    var parent: GroupElement? = null,
    elementHolder: ElementHolder = ElementHolderImpl()
) : ElementHolder by elementHolder, AnimationTarget {

    var pivotX: Float = pivotX
        set(value) {
            field = value
            buildTransformMatrix()
        }
    var pivotY: Float = pivotY
        set(value) {
            field = value
            buildTransformMatrix()
        }
    var rotation: Float = rotation
        set(value) {
            field = value
            buildTransformMatrix()
        }
    var scaleX: Float = scaleX
        set(value) {
            field = value
            buildTransformMatrix()
        }
    var scaleY: Float = scaleY
        set(value) {
            field = value
            buildTransformMatrix()
        }
    var translateX: Float = translateX
        set(value) {
            field = value
            buildTransformMatrix()
        }
    var translateY: Float = translateY
        set(value) {
            field = value
            buildTransformMatrix()
        }

    private val scaleMatrix = Matrix()
    private val originalTransformMatrix = Matrix()
    private val finalTransformMatrix = Matrix()


    fun buildTransformMatrix() {
        originalTransformMatrix.run {
            reset()
            postScale(scaleX, scaleY, pivotX, pivotY)
            postRotate(rotation, pivotX, pivotY)
            postTranslate(translateX, translateY)
        }
        parent?.let {
            originalTransformMatrix.postConcat(it.originalTransformMatrix)
        }

        groupElements.forEach { it.buildTransformMatrix() }
        invalidateTransforms()
    }

    fun scaleAllPaths(scaleMatrix: Matrix) {
        this.scaleMatrix.set(scaleMatrix)
        invalidateTransforms()
    }

    private fun invalidateTransforms() {
        finalTransformMatrix.set(originalTransformMatrix)
        finalTransformMatrix.postConcat(scaleMatrix)

        groupElements.forEach { it.scaleAllPaths(scaleMatrix) }
        pathElements.forEach { it.transform(finalTransformMatrix) }
        clipPathElements.forEach { it.transform(finalTransformMatrix) }
    }
}