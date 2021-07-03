package com.suenara.customvectordrawable.element

import android.graphics.*
import androidx.core.graphics.PathParser

internal class ClipPathElement(
    val name: String?,
    pathData: String?
) {
    val path by lazy { Path(originalPath) }

    private val originalPath = PathParser.createPathFromPathData(pathData) ?: Path()
    private val clipPaint = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun transform(matrix: Matrix) {
        path.set(originalPath)
        path.transform(matrix)
    }

}