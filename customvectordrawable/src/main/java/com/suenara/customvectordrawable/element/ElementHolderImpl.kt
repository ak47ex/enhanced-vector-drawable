package com.suenara.customvectordrawable.element

import android.graphics.Canvas

internal class ElementHolderImpl : ElementHolder {

    override val groupElements = mutableListOf<GroupElement>()
    override val pathElements = mutableListOf<PathElement>()
    override val clipPathElements = mutableListOf<ClipPathElement>()

    override fun addGroup(element: GroupElement) {
        groupElements.add(element)
    }

    override fun addPath(element: PathElement) {
        pathElements.add(element)
    }

    override fun addClipPath(element: ClipPathElement) {
        clipPathElements.add(element)
    }

    override fun scaleAllStrokeWidth(ratio: Float) {
        groupElements.forEach { it.scaleAllStrokeWidth(ratio) }
        pathElements.forEach { it.setStrokeRatio(ratio) }
    }

    override fun draw(canvas: Canvas) {
        clipPathElements.forEach { canvas.clipPath(it.path) }
        groupElements.forEach { it.draw(canvas) }
        pathElements.forEach { it.draw(canvas) }
    }

    override fun findPath(name: String): PathElement? =
        pathElements.find { it.name == name } ?: groupElements.firstNotNullOfOrNull { it.findPath(name) }

    override fun findGroup(name: String): GroupElement? =
        groupElements.find { it.name == name } ?: groupElements.firstNotNullOfOrNull { it.findGroup(name) }

    override fun findClipPath(name: String): ClipPathElement? =
        clipPathElements.find { it.name == name } ?: groupElements.firstNotNullOfOrNull { it.findClipPath(name) }
}