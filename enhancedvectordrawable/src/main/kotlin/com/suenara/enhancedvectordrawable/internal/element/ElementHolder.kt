package com.suenara.enhancedvectordrawable.internal.element

import android.graphics.Canvas

internal interface ElementHolder {
    val groupElements: List<GroupElement>
    val pathElements: List<PathElement>
    val clipPathElements: List<ClipPathElement>


    fun addGroup(element: GroupElement)
    fun addPath(element: PathElement)
    fun addClipPath(element: ClipPathElement)

    fun scaleAllStrokeWidth(ratio: Float)

    fun draw(canvas: Canvas)

    fun findPath(name: String): PathElement?
    fun findGroup(name: String): GroupElement?
    fun findClipPath(name: String): ClipPathElement?
}