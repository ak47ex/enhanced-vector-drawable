package com.suenara.enhancedvectordrawable.internal.animatorparser

import android.animation.*
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.suenara.enhancedvectordrawable.test.R
import org.junit.Test

class AnimatorParserTest {

    private val ctx = ApplicationProvider.getApplicationContext<Context>()
    private val parser = AnimatorParser(ctx)

    @Test
    fun correct_value_animator_read() {
        listOf(
            R.animator.simple_value_animator,
            R.animator.complete_value_animator,
            R.animator.int_value_animator
        ).test<ValueAnimator> { custom, actual ->
            assertThat(custom.animatedFraction).isEqualTo(actual.animatedFraction)
            assertThat(custom.animatedValue).isEqualTo(actual.animatedValue)
            assertThat(custom.currentPlayTime).isEqualTo(actual.currentPlayTime)
            assertThat(custom.repeatCount).isEqualTo(actual.repeatCount)
            assertThat(custom.repeatMode).isEqualTo(actual.repeatMode)
            assertEqualsPvh(custom.values, actual.values)
        }
    }

    @Test
    fun correct_object_animator_read() {
        listOf(
            R.animator.simple_object_animator
        ).test<ObjectAnimator> { custom, actual ->
            assertThat(custom.animatedFraction).isEqualTo(actual.animatedFraction)
            assertThat(custom.animatedValue).isEqualTo(actual.animatedValue)
            assertThat(custom.currentPlayTime).isEqualTo(actual.currentPlayTime)
            assertThat(custom.repeatCount).isEqualTo(actual.repeatCount)
            assertThat(custom.repeatMode).isEqualTo(actual.repeatMode)
            assertEqualsPvh(custom.values, actual.values)
        }
    }

    private inline fun <reified T : Animator> Iterable<Int>.test(assertions: (T, T) -> Unit) = forEach {
        assertEquals(parser.read(it), AnimatorInflater.loadAnimator(ctx, it), assertions)
    }

    private inline fun <reified T : Animator> assertEquals(custom: Animator, actual: Animator, assertions: (T, T) -> Unit) {
        assertThat(custom).isInstanceOf(T::class.java)
        assertThat(actual).isInstanceOf(T::class.java)
        assertAnimatorValuesEqual(custom, actual)
        assertions(custom as T, actual as T)
    }

    private fun assertAnimatorValuesEqual(custom: Animator, actual: Animator) {
        assertThat(custom).isInstanceOf(actual.javaClass)
        assertThat(custom.startDelay).isEqualTo(actual.startDelay)
        assertThat(custom.duration).isEqualTo(actual.duration)
        assertThat(custom.totalDuration).isEqualTo(actual.totalDuration)
        assertThat(custom.isPaused).isEqualTo(actual.isPaused)
        assertThat(custom.isRunning).isEqualTo(actual.isRunning)
        assertThat(custom.isStarted).isEqualTo(actual.isStarted)
        assertThat(custom.listeners).isEqualTo(actual.listeners)
        assertEqualsInterpolators(custom.interpolator, actual.interpolator)
    }

    private fun assertEqualsInterpolators(a: TimeInterpolator, b: TimeInterpolator) {
        assertThat(a).isInstanceOf(b.javaClass)
    }

    private fun assertEqualsPvh(a: Array<PropertyValuesHolder>, b: Array<PropertyValuesHolder>) {
        assertThat(a.size).isEqualTo(b.size)
        a.forEachIndexed { index, pvh ->
            val otherPvh = b[index]
            assertThat(pvh.propertyName).isEqualTo(otherPvh.propertyName)
            assertThat(pvh.toString()).isEqualTo(otherPvh.toString())
        }
    }
}