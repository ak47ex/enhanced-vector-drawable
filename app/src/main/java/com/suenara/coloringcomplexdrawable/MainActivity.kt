package com.suenara.coloringcomplexdrawable

import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.suenara.customvectordrawable.CustomVectorDrawable

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageView>(R.id.check_on).setImageDrawable(getDrawable(R.drawable.vd_check_on))
        findViewById<ImageView>(R.id.check_off).setImageDrawable(getDrawable(R.drawable.vd_check_off))

        findViewById<ImageView>(R.id.custom_check_on).setImageDrawable(CustomVectorDrawable(this, R.drawable.vd_check_on))
        findViewById<ImageView>(R.id.custom_check_off).setImageDrawable(CustomVectorDrawable(this, R.drawable.vd_check_off))
    }
}