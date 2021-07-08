package com.suenara.coloringcomplexdrawable

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.suenara.animatedcustomvectordrawable.CustomAnimatedVectorDrawable
import com.suenara.customvectordrawable.CustomVectorDrawable
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.check_on).setImageDrawable(getDrawable(R.drawable.vd_check_on))
        findViewById<ImageView>(R.id.check_off).setImageDrawable(getDrawable(R.drawable.vd_check_off))

        findViewById<ImageView>(R.id.custom_check_on).setImageDrawable(CustomVectorDrawable(this, R.drawable.vd_check_on))
        findViewById<ImageView>(R.id.custom_check_off).setImageDrawable(CustomVectorDrawable(this, R.drawable.vd_check_off))


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
        findViewById<ImageView>(R.id.custom_animated_check_on).setImageDrawable(CustomAnimatedVectorDrawable(this, R.drawable.avd_check_off_to_on).also {

            it.findAnimations("bg")?.let { animator ->
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

            it.invalidateAnimations()
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        it.stop()
                        it.start()
                    }
                }
            }, 0, repeatTime)
        })
    }

    inline fun <reified T> Any.cast(): T = this as T
}