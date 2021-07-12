package com.suenara.coloringcomplexdrawable

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedStateListDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.Log
import android.util.StateSet
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.suenara.customvectordrawable.CustomAnimatedVectorDrawable
import com.suenara.customvectordrawable.CustomVectorDrawable
import com.suenara.customvectordrawable.changeAnimations
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (getDrawable(R.drawable.animated_checkbox) as StateListDrawable).let { drawable ->
            Log.d("zxc", "${drawable.isStateful}")
        }

        val d = makeAnimated()
        findViewById<CheckBox>(R.id.custom_checkbox).buttonDrawable = d
        findViewById<ImageView>(R.id.check_on).setImageDrawable(getDrawable(R.drawable.vd_check_on))
        findViewById<ImageView>(R.id.check_off).setImageDrawable(getDrawable(R.drawable.vd_check_off))

        findViewById<ImageView>(R.id.custom_check_on).setImageDrawable(
            CustomVectorDrawable(
                this,
                R.drawable.vd_check_on
            )
        )
        findViewById<ImageView>(R.id.custom_check_off).setImageDrawable(
            CustomVectorDrawable(
                this,
                R.drawable.vd_check_off
            )
        )


        val repeatTime = 3000L
        findViewById<ImageView>(R.id.animated_check_on).setImageDrawable(getDrawable(R.drawable.avd_check_off_to_on).also {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        (it as Animatable).stop()
                        (it as Animatable).start()
                    }
                }
            }, 0, repeatTime)
        })
        findViewById<ImageView>(R.id.custom_animated_check_on).setImageDrawable(
            CustomAnimatedVectorDrawable(
                this,
                R.drawable.avd_check_off_to_on
            ).modifyColors().also {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            it.start()
                        }
                    }
                }, 1500)
            })
    }

    private fun makeAnimated(): StateListDrawable {
        val an = AnimatedStateListDrawable()
        an.addState(
            intArrayOf(android.R.attr.state_checked),
            CustomVectorDrawable(this, R.drawable.vd_check_on)!!,
            R.drawable.vd_check_on
        )
        an.addState(StateSet.NOTHING, CustomVectorDrawable(this, R.drawable.vd_check_off)!!, R.drawable.vd_check_off)
        an.addTransition(
            R.drawable.vd_check_on,
            R.drawable.vd_check_off,
            CustomAnimatedVectorDrawable(this, R.drawable.avd_check_on_to_off).modifyColors(),
            false
        )
        an.addTransition(
            R.drawable.vd_check_off,
            R.drawable.vd_check_on,
            CustomAnimatedVectorDrawable(this, R.drawable.avd_check_off_to_on).modifyColors(),
            false
        )
        an.jumpToCurrentState()
        return an
    }

    private fun CustomAnimatedVectorDrawable.modifyColors() = also {
        it.changeAnimations("bg") { animator ->
            (animator as? AnimatorSet)?.childAnimations?.find { it is ObjectAnimator }?.cast<ObjectAnimator>()
                ?.let { objAnim ->
                    objAnim.values.filter { pvh -> pvh.propertyName == "strokeColor" }.forEach { pvh ->
                        pvh.setIntValues(Color.RED, Color.GREEN, Color.CYAN)
                    }
                }
        }
        it.findPath("fg")?.let { path ->
            path.strokeColor = Color.BLACK
        }
    }

    inline fun <reified T> Any.cast(): T = this as T
}