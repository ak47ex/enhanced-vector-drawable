package com.suenara.customvectordrawable

import android.content.res.Resources
import androidx.annotation.XmlRes
import com.suenara.customvectordrawable.element.ClipPathElement
import com.suenara.customvectordrawable.element.GroupElement
import com.suenara.customvectordrawable.element.PathElement
import com.suenara.customvectordrawable.element.Shape
import com.suenara.customvectordrawable.elementparser.ClipPathElementParser
import com.suenara.customvectordrawable.elementparser.GroupElementParser
import com.suenara.customvectordrawable.elementparser.PathElementParser
import com.suenara.customvectordrawable.elementparser.VectorElementParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*

internal class VectorDrawableParser(private val resources: Resources) {

    @Throws(Resources.NotFoundException::class, XmlPullParserException::class)
    fun readShape(@XmlRes resId: Int): Shape {
        val parser = resources.getXml(resId)
        val groupParser = GroupElementParser()
        val pathParser = PathElementParser()
        val clipPathParser = ClipPathElementParser()

        var shape = Shape(null, 0f, 0f, 0, 0f, 0f)
        var pathElement: PathElement? = null
        var clipPathElement: ClipPathElement? = null
        val groupElementStack = Stack<GroupElement>()

        try {
            var event = parser.eventType

            while (event != XmlPullParser.END_DOCUMENT) {
                val name = parser.name

                when (event) {
                    XmlPullParser.START_TAG -> {
                        when (Element.of(name)) {
                            Element.VECTOR -> {
                                shape = VectorElementParser().read(parser)
                            }
                            Element.GROUP -> {
                                val group = groupParser.read(parser)
                                groupElementStack.push(group)
                            }
                            Element.PATH -> {
                                pathElement = pathParser.read(parser)
                            }
                            Element.CLIP_PATH -> {
                                clipPathElement = clipPathParser.read(parser)
                            }
                            null -> Unit
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        when (Element.of(name)) {
                            Element.VECTOR -> {
                                shape.buildTransformMatrices()
                            }
                            Element.GROUP -> {
                                val gm = groupElementStack.pop()
                                if (groupElementStack.isEmpty()) {
                                    gm.parent = null
                                    shape.addGroup(gm)
                                } else {
                                    gm.parent = groupElementStack.peek()
                                    groupElementStack.peek().addGroup(gm)
                                }
                            }
                            Element.PATH -> {
                                val pm = requireNotNull(pathElement)
                                if (groupElementStack.isEmpty()) {
                                    shape.addPath(pm)
                                } else {
                                    groupElementStack.peek().addPath(pm)
                                }
                                shape.appendToFullPath(pm.path)
                            }
                            Element.CLIP_PATH -> {
                                val cpm = requireNotNull(clipPathElement)
                                if (groupElementStack.isEmpty()) {
                                    shape.addClipPath(cpm)
                                } else {
                                    groupElementStack.peek().addClipPath(cpm)
                                }
                            }
                            null -> Unit
                        }
                    }
                }

                event = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            parser.close()
        }
        return shape
    }

    private enum class Element(val tag: String) {
        VECTOR("vector"), GROUP("group"), PATH("path"), CLIP_PATH("clip-path");

        companion object {
            fun of(name: String): Element? = values().firstOrNull { it.tag == name }
        }
    }
}