package com.suenara.enhancedvectordrawable.internal

import android.util.LruCache

internal class ResourceCache<T> {

    private val cache = LruCache<Int, T>(10)

    operator fun get(key: Int): T? = cache[key]

    operator fun set(key: Int, value: T) {
        cache.put(key, value)
    }
}