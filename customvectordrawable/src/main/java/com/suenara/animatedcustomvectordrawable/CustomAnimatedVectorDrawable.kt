package com.suenara.animatedcustomvectordrawable

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.ArrayMap
import android.util.Log
import androidx.annotation.DrawableRes
import com.suenara.customvectordrawable.CustomVectorDrawable
import com.suenara.customvectordrawable.element.GroupElement
import com.suenara.customvectordrawable.element.PathElement

@SuppressLint("ResourceType")
class CustomAnimatedVectorDrawable
constructor(private val context: Context, @DrawableRes private val resId: Int) : Drawable(), Animatable {

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

    private val drawable: CustomVectorDrawable
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

    override fun setAlpha(alpha: Int) {
        TODO("Not yet implemented")
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        TODO("Not yet implemented")
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        drawable.bounds = bounds
    }

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

    fun findAnimations(targetName: String): Animator? {
        return targetNameMap.values.indexOf(targetName).takeIf { it >= 0 }?.let {
            targetNameMap.keyAt(it)
        }
    }

    fun invalidateAnimations() {
        animatorSetFromXml = AnimatorSet().also {
            prepareLocalAnimators(it)
        }

        animator = CustomAnimatorSet(this, animatorSetFromXml)
    }

    fun findPath(name: String): CustomVectorDrawable.Path? = drawable.findPath(name)

    private class CustomAnimatorSet(private val drawable: Drawable, set: AnimatorSet) {
        private val animatorSet: AnimatorSet = set.clone()
        private val isInfinite = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            animatorSet.totalDuration == Animator.DURATION_INFINITE
        } else {
            Log.w("zxc", "might be wrong!!!")
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