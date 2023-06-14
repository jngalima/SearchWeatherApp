package com.example.weatherapp.data.repository

import android.graphics.Bitmap

class IconCache {

    /**
     * will store icon id's and bitmap associated with it
     */
    private val iconMap = mutableMapOf<String, Bitmap>()

    /**
     * fetch bitmap from cache if it exists else return null
     */
    fun getImageFromCache(iconId: String): Bitmap? = iconMap[iconId]

    /**
     * add bitmap to cache if fetched from api
     */
    fun addToCache(iconId: String, icon: Bitmap){
        iconMap[iconId] = icon
    }
}