package com.suenara.customvectordrawable.element

import android.graphics.Matrix
import com.suenara.customvectordrawable.CustomVectorDrawable

internal class GroupElement(
    val name: String?,
    val pivotX: Float,
    val pivotY: Float,
    val rotation: Float,
    val scaleX: Float,
    val scaleY: Float,
    val translateX: Float,
    val translateY: Float,
    var parent: GroupElement? = null,
    elementHolder: ElementHolder = ElementHolderImpl()
) : ElementHolder by elementHolder, CustomVectorDrawable.Target {

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
    }

    fun scaleAllPaths(scaleMatrix: Matrix) {
        this.scaleMatrix.set(scaleMatrix)
        finalTransformMatrix.set(originalTransformMatrix)
        finalTransformMatrix.postConcat(scaleMatrix)

        groupElements.forEach { it.scaleAllPaths(scaleMatrix) }
        pathElements.forEach { it.transform(finalTransformMatrix) }
        clipPathElements.forEach { it.transform(finalTransformMatrix) }
    }
}