package com.suenara.animatedcustomvectordrawable

import android.animation.ArgbEvaluator
import android.animation.PropertyValuesHolder
import android.animation.TypeEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.XmlResourceParser
import android.util.Log
import android.view.InflateException
import androidx.core.graphics.PathParser
import androidx.core.graphics.PathParser.PathDataNode
import com.suenara.customvectordrawable.attributeIndices

class PropertyValuesHolderParser(private val context: Context) {
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    @SuppressLint("Recycle")
    fun read(parser: XmlResourceParser): PropertyValuesHolder? {
        val propertyName = AnimatorAttribute.PropertyName.get(context, parser)
        val from = AnimatorAttribute.ValueFrom.get(context, parser)
        val to = AnimatorAttribute.ValueTo.get(context, parser)


        var parsedValueType = AnimatorAttribute.ValueType.get(context, parser)

        val valueType = when {
            parsedValueType is AnimatorValue.Undefined && (from is AnimatorValue.Color || to is AnimatorValue.Color) -> {
                AnimatorValue.Color(0)
            }
            parsedValueType is AnimatorValue.Undefined -> AnimatorValue.FloatNumber(0f)
            else -> parsedValueType
        }

        val getFloats = valueType is AnimatorValue.FloatNumber
        var returnValue: PropertyValuesHolder? = null
        if (valueType is AnimatorValue.Path) {
            val nodesFrom = getPathDataNodes(from)
            val nodesTo = getPathDataNodes(to)

            if (nodesFrom != null || nodesTo != null) {
                if (nodesFrom != null) {
                    val evaluator = PathDataEvaluator()
                    if (nodesTo != null) {
                        if (!PathParser.canMorph(nodesFrom, nodesTo)) {
                            throw InflateException("Can't morph from ${(from as? AnimatorValue.Path)?.value} to ${(to as? AnimatorValue.Path)?.value}")
                        }
                        returnValue = PropertyValuesHolder.ofObject(propertyName, evaluator, nodesFrom, nodesTo)
                    } else {
                        returnValue = PropertyValuesHolder.ofObject(propertyName, evaluator, (nodesFrom as Object))
                    }
                } else if (nodesTo != null) {
                    val evaluator = PathDataEvaluator()
                    returnValue = PropertyValuesHolder.ofObject(propertyName, evaluator, (nodesTo as Object))
                }
            }
        } else {
            var evaluator: TypeEvaluator<*>? = null

            if (valueType is AnimatorValue.Color) {
                evaluator = ArgbEvaluator()
            }
            if (getFloats) {
                var valueFrom = 0f
                var valueTo = 0f

                if (from != null) {
                    valueFrom = (from as AnimatorValue.FloatNumber).value
                    if (to != null) {
                        valueTo = (to as AnimatorValue.FloatNumber).value
                        returnValue = PropertyValuesHolder.ofFloat(propertyName, valueFrom, valueTo)
                    } else {
                        returnValue = PropertyValuesHolder.ofFloat(propertyName, valueFrom)
                    }
                } else {
                    valueTo = (to as AnimatorValue.FloatNumber).value
                    returnValue = PropertyValuesHolder.ofFloat(propertyName, valueFrom, valueTo)
                }
            } else {
                var valueFrom = 0
                var valueTo = 0
                if (from != null) {
                    valueFrom = if (from is AnimatorValue.Color) {
                        (from as AnimatorValue.Color).value
                    } else {
                        (from as AnimatorValue.IntNumber).value
                    }
                    if (to != null) {
                        valueTo = if (to is AnimatorValue.Color) {
                            (to as AnimatorValue.Color).value
                        } else {
                            (to as AnimatorValue.IntNumber).value
                        }
                        returnValue = PropertyValuesHolder.ofInt(propertyName, valueFrom, valueTo)
                    } else {
                        returnValue = PropertyValuesHolder.ofInt(propertyName, valueFrom)
                    }
                } else {
                    if (to != null) {
                        valueTo = if (to is AnimatorValue.Color) {
                            (to as AnimatorValue.Color).value
                        } else {
                            (to as AnimatorValue.IntNumber).value
                        }
                        returnValue = PropertyValuesHolder.ofInt(propertyName, valueTo)
                    }
                }
            }
            if (returnValue != null && evaluator != null) {
                returnValue.setEvaluator(evaluator)
            }
        }

        return returnValue
    }

    private fun getPathDataNodes(value: AnimatorValue<*>?): Array<PathDataNode>? {
        return PathParser.createNodesFromPathData((value as? AnimatorValue.Path)?.value)
    }

    private class PathDataEvaluator
        @JvmOverloads constructor(private var nodeArray: Array<PathDataNode>? = null) : TypeEvaluator<Array<PathDataNode>> {


        override fun evaluate(
            fraction: Float,
            startValue: Array<PathDataNode>?,
            endValue: Array<PathDataNode>?
        ): Array<PathDataNode> {
            if (!PathParser.canMorph(startValue, endValue)) {
                throw IllegalArgumentException("Can't interpolate between two incompatible pathData")
            }

            if (!PathParser.canMorph(nodeArray, startValue)) {
                nodeArray = PathParser.deepCopyNodes(startValue)
            }
            val nodeArray = requireNotNull(nodeArray)
            requireNotNull(startValue)
            for (i in startValue.indices) {
                if (endValue != null) {
                    nodeArray[i].interpolatePathDataNode(startValue[i], endValue[i], fraction)
                }
            }

            return nodeArray
        }
    }
}