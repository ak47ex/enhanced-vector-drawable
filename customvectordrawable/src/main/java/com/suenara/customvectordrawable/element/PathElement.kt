package com.suenara.customvectordrawable.element

import android.graphics.*
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.graphics.PathParser
import androidx.core.graphics.alpha
import com.suenara.customvectordrawable.CustomVectorDrawable
import com.suenara.customvectordrawable.floatAlphaToInt

internal class PathElement(
    val name: String?,
    private var fillAlpha: Int,
    @ColorInt fillColor: Int,
    val fillType: Path.FillType,
    val pathData: String?,
    var strokeAlpha: Int,
    @ColorInt strokeColor: Int,
    val strokeLineCap: Paint.Cap,
    val strokeLineJoin: Paint.Join,
    val strokeMiterLimit: Float,
    strokeWidth: Float,
    trimPathEnd: Float,
    trimPathOffset: Float,
    trimPathStart: Float
): CustomVectorDrawable.Path, CustomVectorDrawable.Target {
    var strokeWidth: Float = strokeWidth
        set(value) {
            field = value
            updatePaint()
        }

    var trimPathEnd: Float = trimPathEnd
        set(value) {
            field = value
            trimPath()
        }
    var trimPathOffset: Float = trimPathOffset
        set(value) {
            field = value
            trimPath()
        }
    var trimPathStart: Float = trimPathStart
        set(value) {
            field = value
            trimPath()
        }
    override var fillColor: Int = fillColor
        set(value) {
            field = value
            fillAlpha = value.alpha
            updatePaint()
        }
    override var strokeColor: Int = strokeColor
        set(value) {
            field = value
            strokeAlpha = value.alpha
            updatePaint()
        }


    var isFillAndStroke: Boolean = false
        private set
    val path: Path by lazy { Path(originalPath) }
    val pathPaint: Paint = Paint().apply {
        isAntiAlias = true
    }

    private val originalPath: Path = PathParser.createPathFromPathData(pathData).also {
        it.fillType = fillType
    }
    private val scaleMatrix: Matrix = Matrix().apply { reset() }
    private val trimmedPath: Path = Path(path)
    private var strokeRatio: Float = 1f

    init {
        updatePaint()
    }

    fun transform(matrix: Matrix) {
        scaleMatrix.set(matrix)
        trimPath()
    }

    fun setStrokeRatio(ratio: Float) {
        strokeRatio = ratio
        updatePaint()
    }

    fun draw(canvas: Canvas) {
        if (isFillAndStroke) {
            makeFillPaint()
            canvas.drawPath(path, pathPaint)
            makeStrokePaint()
            canvas.drawPath(path, pathPaint)
        } else {
            canvas.drawPath(path, pathPaint)
        }
    }

    fun setPathData(pathData: Array<PathParser.PathDataNode>) {
        path.reset()
        PathParser.PathDataNode.nodesToPath(pathData, path)
        path.transform(scaleMatrix)
    }

    private fun trimPath() {
        if (trimPathStart == 0f && trimPathEnd == 1f && trimPathOffset == 0f) {
            path.set(originalPath)
            path.transform(scaleMatrix)
        } else {
            val pathMeasure = PathMeasure(originalPath, false)
            val length = pathMeasure.length
            trimmedPath.reset()
            pathMeasure.getSegment(
                (trimPathStart + trimPathOffset) * length,
                (trimPathEnd + trimPathOffset) * length,
                trimmedPath,
                true
            )
            path.set(trimmedPath)
            path.transform(scaleMatrix)
        }
    }

    private fun updatePaint() {
        pathPaint.strokeWidth = strokeWidth * strokeRatio;

        if (fillColor != Color.TRANSPARENT && strokeColor != Color.TRANSPARENT) {
            isFillAndStroke = true
        } else if (fillColor != Color.TRANSPARENT) {
            pathPaint.color = fillColor
            pathPaint.alpha = fillAlpha
            pathPaint.style = Paint.Style.FILL
            isFillAndStroke = false
        } else if (strokeColor != Color.TRANSPARENT) {
            pathPaint.color = strokeColor
            pathPaint.alpha = strokeAlpha
            pathPaint.style = Paint.Style.STROKE
            isFillAndStroke = false
        } else {
            pathPaint.color = Color.TRANSPARENT
        }

        pathPaint.strokeCap = strokeLineCap
        pathPaint.strokeJoin = strokeLineJoin
        pathPaint.strokeMiter = strokeMiterLimit
    }

    private fun makeFillPaint() = with(pathPaint) {
        color = fillColor
        alpha = fillAlpha
        style = Paint.Style.FILL
    }

    private fun makeStrokePaint() = with(pathPaint) {
        color = strokeColor
        alpha = strokeAlpha
        style = Paint.Style.STROKE
    }
}