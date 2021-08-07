package com.suenara.enhancedvectordrawable

import android.animation.Animator

interface VectorAnimationContainer : VectorPathContainer {
    fun findAnimations(targetName: String): Animator?
    fun invalidateAnimations()
}