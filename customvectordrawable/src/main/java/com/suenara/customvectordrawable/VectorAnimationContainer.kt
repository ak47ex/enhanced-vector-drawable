package com.suenara.customvectordrawable

import android.animation.Animator

interface VectorAnimationContainer : VectorPathContainer {
    fun findAnimations(targetName: String): Animator?
    fun invalidateAnimations()
}

inline fun VectorAnimationContainer.changeAnimations(targetName: String, crossinline action: (Animator) -> Unit) {
    findAnimations(targetName)?.let {
        action(it)
        invalidateAnimations()
    }
}