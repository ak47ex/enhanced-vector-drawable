package com.suenara.enhancedvectordrawable.internal.element

import android.graphics.Matrix
import androidx.annotation.Keep
import com.suenara.enhancedvectordrawable.AnimationTarget

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

    constructor(prototype: GroupElement) : this(
        prototype.name,
        prototype.pivotX,
        prototype.pivotY,
        prototype.rotation,
        prototype.scaleX,
        prototype.scaleY,
        prototype.translateX,
        prototype.translateY,
        prototype.parent?.let { GroupElement(it) },
        ElementHolderImpl(prototype)
    ) {
        scaleMatrix.set(prototype.scaleMatrix)
        originalTransformMatrix.set(prototype.originalTransformMatrix)
        finalTransformMatrix.set(prototype.finalTransformMatrix)
    }

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