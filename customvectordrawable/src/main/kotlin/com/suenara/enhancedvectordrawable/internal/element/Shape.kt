package com.suenara.enhancedvectordrawable.internal.element

import android.graphics.Matrix
import android.graphics.Path
import androidx.annotation.Keep
import com.suenara.enhancedvectordrawable.AnimationTarget

@Keep
internal class Shape(
    val name: String?,
    val viewportWidth: Float,
    val viewportHeight: Float,
    var alpha: Int,
    val width: Float,
    val height: Float,
    elementHolder: ElementHolder = ElementHolderImpl()
) : ElementHolder by elementHolder, AnimationTarget {
    private val fullPath: Path = Path()
    private val scaleMatrix: Matrix = Matrix()

    fun appendToFullPath(path: Path) = fullPath.addPath(path)

    fun buildTransformMatrices() {
        groupElements.forEach { it.buildTransformMatrix() }
    }

    fun scaleAllPaths(scaleMatrix: Matrix) {
        this.scaleMatrix.set(scaleMatrix)

        groupElements.forEach { it.scaleAllPaths(scaleMatrix) }
        pathElements.forEach { it.transform(scaleMatrix) }
        clipPathElements.forEach { it.transform(scaleMatrix) }
    }


    companion object {
        val EMPTY = Shape(null, 0f, 0f, 0, 0f, 0f)
    }
}