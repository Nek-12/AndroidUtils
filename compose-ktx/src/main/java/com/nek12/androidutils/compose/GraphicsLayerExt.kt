package com.nek12.androidutils.compose

import androidx.compose.ui.graphics.GraphicsLayerScope

/**
 * Scale the element uniformly. Alias for [GraphicsLayerScope.scaleX] and [GraphicsLayerScope.scaleY].
 * Getter returns the average value of the scale
 */
var GraphicsLayerScope.scale: Float
    get() = (scaleX + scaleY) / 2f
    set(value) {
        scaleX = value
        scaleY = value
    }
