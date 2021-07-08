package com.suenara.customvectordrawable

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.graphics.withTranslation
import com.suenara.customvectordrawable.element.Shape

class CustomVectorDrawable
@Throws(Resources.NotFoundException::class)
constructor(private val resources: Resources, @DrawableRes private val resId: Int) : Drawable() {

    private val shape: Shape
    private var left: Int = 0
    private var top: Int = 0
    private var width: Int = 0
    private var height: Int = 0
    private var scaleRatio: Float = 1f
    private var strokeRatio: Float = 1f
    private val scaleMatrix = Matrix()

    init {
        shape = if (resId != 0) buildShape(resId) else Shape.EMPTY
        setBounds(0, 0, dp(shape.width), dp(shape.height))
    }

    constructor(context: Context, resId: Int) : this(context.resources, resId)

    override fun draw(canvas: Canvas) {
        alpha = shape.alpha
        if (left != 0 || top != 0) {
            canvas.withTranslation(left.toFloat(), top.toFloat()) {
                shape.draw(canvas)
            }
        } else {
            shape.draw(canvas)
        }
    }

    override fun setAlpha(alpha: Int) {
        shape.alpha = alpha
    }

    override fun setTintList(tint: ColorStateList?) {
        TODO("Not implemented yet")
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        TODO("Not implemented yet")
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        if (bounds.width() != 0 && bounds.height() != 0) {
            left = bounds.left
            top = bounds.top

            width = bounds.width()
            height = bounds.height()

            buildScaleMatrix()
            scaleAllPaths()
            scaleAllStrokes()
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    override fun getIntrinsicWidth(): Int = dp(shape.width)
    override fun getIntrinsicHeight(): Int = dp(shape.height)
    override fun getConstantState(): ConstantState {
        return object : ConstantState() {
            override fun newDrawable(): Drawable = CustomVectorDrawable(resources, resId)

            override fun newDrawable(res: Resources?): Drawable {
                return res?.let { CustomVectorDrawable(it, resId) } ?: newDrawable()
            }

            override fun getChangingConfigurations(): Int = 0
        }
    }

    fun findPath(name: String): Path? = shape.findPath(name)

    internal fun findTarget(name: String): Target? {
        return if (shape.name == name) {
            shape
        } else {
            shape.findGroup(name) ?: shape.findPath(name) ?: shape.findClipPath(name)
        }
    }

    fun getPixelSize(): Float {
        if (
            shape.width == 0f ||
            shape.height == 0f ||
            shape.viewportHeight == 0f ||
            shape.viewportWidth == 0f
        ) {
            return 1f // fall back to 1:1 pixel mapping.
        }
        val intrinsicWidth = dp(shape.width)
        val intrinsicHeight = dp(shape.height)
        val viewportWidth = shape.viewportWidth
        val viewportHeight = shape.viewportHeight
        val scaleX = viewportWidth / intrinsicWidth
        val scaleY = viewportHeight / intrinsicHeight
        return minOf(scaleX, scaleY)
    }

    @Throws(Resources.NotFoundException::class)
    private fun buildShape(resId: Int) = VectorDrawableParser(resources).readShape(resId)

    private fun buildScaleMatrix() {
        scaleMatrix.run {
            reset()
            postTranslate(width / 2f - shape.viewportWidth / 2f, height / 2f - shape.viewportHeight / 2f)

            val widthRatio = width / shape.viewportWidth
            val heightRatio = height / shape.viewportHeight
            scaleRatio = minOf(widthRatio, heightRatio)

            postScale(scaleRatio, scaleRatio, width / 2f, height / 2f)
        }
    }

    private fun scaleAllPaths() = shape.scaleAllPaths(scaleMatrix)

    private fun scaleAllStrokes() {
        strokeRatio = minOf(width / shape.width, height / shape.height)
        shape.scaleAllStrokeWidth(strokeRatio)
    }

    private fun dp(value: Float): Int = (resources.displayMetrics.density * value).toInt()

    interface Target

    interface Path : Target {
        @get:ColorInt
        @setparam:ColorInt
        var fillColor: Int

        @get:ColorInt
        @setparam:ColorInt
        var strokeColor: Int
    }
}