package com.suenara.enhancedvectordrawable.internal

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.util.ArrayMap
import android.util.Log
import androidx.annotation.XmlRes
import com.suenara.enhancedvectordrawable.EnhancedVectorDrawable
import com.suenara.enhancedvectordrawable.internal.animatorparser.AnimatorParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

internal class AnimatedVectorDrawableParser(private val context: Context, private val theme: Resources.Theme? = null) {
    private val resources: Resources = context.resources

    private fun readCustomVectorDrawable(parser: XmlResourceParser): EnhancedVectorDrawable {
        for (attrIndex in 0 until parser.attributeCount) {
            if (parser.getAttributeName(attrIndex) == DRAWABLE) {
                val drawableRes = parser.getAttributeResourceValue(attrIndex, 0)
                if (drawableRes != 0) {
                    return EnhancedVectorDrawable(resources, drawableRes)
                }
                break
            }
        }
        //TODO: maybe return null?
        throw IllegalStateException()
    }

    fun read(@XmlRes resId: Int): ParsedResource {
        val parser = resources.getXml(resId)

        var type: Int = parser.next()
        while (type != XmlPullParser.START_TAG && type != XmlPullParser.END_DOCUMENT) {
            type = parser.next()
        }
        if (type != XmlPullParser.START_TAG) {
            throw XmlPullParserException("No start tag found")
        }

        var cvd: EnhancedVectorDrawable? = null
        var pathErrorScale = 1f

        val animators = ArrayList<Animator>()
        val targetNameMap = ArrayMap<Animator, String>()
        try {
            var event = parser.eventType
            val innerDepth = parser.depth

            while(event != XmlPullParser.END_DOCUMENT && (parser.depth >= innerDepth || event != XmlPullParser.END_TAG)) {
                if (event != XmlPullParser.START_TAG) {
                    event = parser.next()
                    continue
                }

                when(parser.name) {
                    ANIMATED_VECTOR -> {
                        cvd = readCustomVectorDrawable(parser).apply {
                            pathErrorScale = getPixelSize()
                        }
                    }
                    TARGET -> {
                        var target: String? = null
                        for (attrIndex in 0 until parser.attributeCount) {
                            val attrName = parser.getAttributeName(attrIndex)
                            if (attrName == NAME) {
                                target = parser.getAttributeValue(attrIndex)
                            } else if (attrName == ANIMATION) {
                                val animatorRes = parser.getAttributeResourceValue(attrIndex, 0)
                                if (animatorRes != 0) {
                                    val animator = AnimatorInflater.loadAnimator(context, animatorRes).let {
                                        if (shouldParseManually(it)) {
                                            parseAnimator(animatorRes)
                                        } else {
                                            it
                                        }
                                    }
                                    animators.add(animator)
                                    targetNameMap[animator] = target
                                }
                            } else {
                                Log.w(TAG, "unknown attribute '$attrName'. Skipping")
                            }
                        }
                    }
                }
                event = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            throw e
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        } finally {
            parser.close()
        }
        return ParsedResource(requireNotNull(cvd) { "VectorDrawable was not found in XML" }, animators, targetNameMap)
    }

    private fun shouldParseManually(animator: Animator): Boolean {
        val isPathDataAnimator: (Animator) -> Boolean = {
            (it as? ObjectAnimator)?.propertyName == PATH_DATA_PROPERTY_NAME
        }

        return (animator as? AnimatorSet)?.let { set ->
            set.childAnimations.any { anim ->
                isPathDataAnimator(anim)
            }
        } ?: isPathDataAnimator(animator)
    }

    private fun parseAnimator(resId: Int): Animator = AnimatorParser(context).read(resId)

    data class ParsedResource(
        val drawable: EnhancedVectorDrawable,
        val animators: ArrayList<Animator>,
        val targetNameMap: ArrayMap<Animator, String>
    )

    companion object {
        private const val ANIMATED_VECTOR = "animated-vector"
        private const val DRAWABLE = "drawable"
        private const val NAME = "name"
        private const val ANIMATION = "animation"
        private const val TARGET = "target"
        private const val PATH_DATA_PROPERTY_NAME = "pathData"

        private val TAG = javaClass.simpleName
    }
}