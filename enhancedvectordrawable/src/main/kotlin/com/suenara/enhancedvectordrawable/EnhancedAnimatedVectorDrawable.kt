package com.suenara.enhancedvectordrawable

import android.animation.Animator
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.ArrayMap
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.suenara.enhancedvectordrawable.internal.AnimatedVectorDrawableParser
import com.suenara.enhancedvectordrawable.internal.element.GroupElement
import com.suenara.enhancedvectordrawable.internal.element.PathElement

/**
 * [EnhancedAnimatedVectorDrawable] is an animated vector drawable that open to modification it's vector structure
 * It behaves like an ordinary [android.graphics.drawable.AnimatedVectorDrawable] with few exceptions:
 * — there is available such option as change vector path configuration (look at [findPath] and [VectorPath])
 * — there is available to change whole animator and any of it's property ([findAnimations])
 * — there is no tint support (TBD)
 */
@Keep
@SuppressLint("ResourceType")
class EnhancedAnimatedVectorDrawable
constructor(context: Context, @DrawableRes private val resId: Int) : Drawable(), Animatable, VectorAnimationContainer {

    private val shouldIgnoreInvalidAnim = true

    private val callback = object : Callback {
        override fun invalidateDrawable(who: Drawable) {
            invalidateSelf()
        }

        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
            scheduleSelf(what, `when`)
        }

        override fun unscheduleDrawable(who: Drawable, what: Runnable) {
            unscheduleSelf(what)
        }
    }

    private val drawable: EnhancedVectorDrawable
    private val animators: ArrayList<Animator>
    private val targetNameMap: ArrayMap<Animator, String>
    private var animatorSetFromXml: AnimatorSet
    private var animator: CustomAnimatorSet

    init {
        val result = AnimatedVectorDrawableParser(context).read(resId)
        result.drawable.callback = callback
        drawable = result.drawable
        animators = result.animators
        targetNameMap = result.targetNameMap

        animatorSetFromXml = AnimatorSet().also {
            prepareLocalAnimators(it)
        }

        animator = CustomAnimatorSet(this, animatorSetFromXml)
    }

    private fun prepareLocalAnimators(animatorSet: AnimatorSet) {
        val count = animators.size
        if (count > 0) {
            val firstAnim = prepareLocalAnimator(0)
            val builder = animatorSet.play(firstAnim)
            for (i in 1 until count) {
                val nextAnim = prepareLocalAnimator(i)
                builder.with(nextAnim)
            }
        }
    }

    private fun prepareLocalAnimator(index: Int): Animator {
        val animator = animators[index]
        val localAnimator = animator.clone()
        val targetName = targetNameMap[animator]
        val target = targetName?.let { drawable.findTarget(it) }
        if (shouldIgnoreInvalidAnim) {
            if (target == null) {
                throw IllegalStateException(
                    "Target with the name \"$targetName\" cannot be found in the VectorDrawable to be animated."
                )
            } else if (target !is GroupElement && target !is PathElement) {
                throw UnsupportedOperationException(
                    "Target should be either VGroup, VPath, or ConstantState, ${target.javaClass} is not supported"
                )
            }
        }
        localAnimator.setTarget(target)
        return localAnimator
    }

    //region drawable

    override fun draw(canvas: Canvas) {
        animator.onDraw(canvas)
        drawable.draw(canvas)
    }

    override fun getAlpha(): Int {
        return drawable.alpha
    }

    override fun setAlpha(alpha: Int) {
        drawable.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        drawable.colorFilter = colorFilter
    }

    override fun getColorFilter(): ColorFilter? {
        return drawable.colorFilter
    }

    override fun setTintList(tint: ColorStateList?) {
        drawable.setTintList(tint)
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        drawable.setTintMode(tintMode)
    }

    override fun jumpToCurrentState() {
        animator.end()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        drawable.bounds = bounds
    }

    override fun onStateChange(state: IntArray): Boolean {
        return drawable.setState(state)
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        if (animator.isInfinite() && animator.isStarted()) {
            if (visible) {
                animator.resume()
            } else {
                animator.pause()
            }
        }
        drawable.setVisible(visible, restart)
        return super.setVisible(visible, restart)
    }

    override fun getDirtyBounds(): Rect = drawable.dirtyBounds

    override fun getIntrinsicWidth(): Int = drawable.intrinsicWidth

    override fun getIntrinsicHeight(): Int = drawable.intrinsicHeight

    override fun getMinimumWidth(): Int = drawable.minimumWidth

    override fun getMinimumHeight(): Int = drawable.minimumHeight

    //endregion

    //region animatable
    override fun start() {
        animator.start()
    }

    override fun stop() {
        animator.end()
    }

    override fun isRunning(): Boolean = animator.isRunning()

    //endregion

    /**
     * Method that returns Animator of any path with [targetName] that defined in xml
     * Example how change "strokeColor" property animation of "background" path and set values from Magenta to Green:
     *    drawable.findAnimations("background")?.let { animator ->
     *        (animator as? AnimatorSet)?.childAnimations
     *            ?.find { it is ObjectAnimator }
     *            ?.let { it as ObjectAnimator }
     *            ?.let { objAnim ->
     *               objAnim.changeProperty("strokeColor") { pvh ->
     *                   pvh.setIntValues(Color.MAGENTA, Color.GREEN)
     *               }
     *            }
     *        }
     *        drawable.invalidateAnimations()
     *    }
     *
     * For simple API look at [com.vk.im.ui.drawables.vectordrawable.changeAnimations]
     */
    override fun findAnimations(targetName: String): Animator? {
        return targetNameMap.values.indexOf(targetName).takeIf { it >= 0 }?.let {
            targetNameMap.keyAt(it)
        }
    }

    /**
     * Applies any changes in animators
     */
    override fun invalidateAnimations() {
        animatorSetFromXml = AnimatorSet().also {
            prepareLocalAnimators(it)
        }

        animator = CustomAnimatorSet(this, animatorSetFromXml)
    }

    override fun findPath(name: String): VectorPath? = drawable.findPath(name)

    override fun invalidatePath() = invalidateSelf()

    private class CustomAnimatorSet(private val drawable: Drawable, set: AnimatorSet) {
        private val animatorSet: AnimatorSet = set.clone()
        private val isInfinite = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            animatorSet.totalDuration == Animator.DURATION_INFINITE
        } else {
            animatorSet.duration == -1L
        }

        fun start() {
            if (!animatorSet.isStarted) {
                animatorSet.start()
                invalidateOwningView()
            }
        }

        fun end() {
            animatorSet.end()
        }

        fun reset() {
            start()
            animatorSet.cancel()
        }

        fun reverse() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                animatorSet.reverse()
            }
            invalidateOwningView()
        }

        fun onDraw(canvas: Canvas) {
            if (animatorSet.isStarted) invalidateOwningView()
        }

        fun isStarted() = animatorSet.isStarted

        fun isRunning() = animatorSet.isRunning

        fun isInfinite() = isInfinite

        fun pause() {
            animatorSet.pause()
        }

        fun resume() {
            animatorSet.resume()
        }

        private fun invalidateOwningView() {
            drawable.invalidateSelf()
        }
    }

}