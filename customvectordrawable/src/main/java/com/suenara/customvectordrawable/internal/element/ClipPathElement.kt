package com.suenara.customvectordrawable.internal.element

import android.graphics.*
import androidx.core.graphics.PathParser
import com.suenara.customvectordrawable.CustomVectorDrawable

internal class ClipPathElement(
    val name: String?,
    pathData: String?
) : CustomVectorDrawable.Target {
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