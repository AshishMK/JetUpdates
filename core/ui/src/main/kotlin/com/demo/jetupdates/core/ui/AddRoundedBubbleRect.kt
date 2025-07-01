/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demo.jetupdates.core.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlin.math.min

fun Path.addRoundedBubbleRect(
    contentRect: Size,
    density: Float,
    alignment: ArrowAlignment,
) {
    // val alignment = state.alignment

    val cornerRadius = 12.dp // = state.cornerRadius

    val width = contentRect.width
    val height = contentRect.height
    val left = 0
    val right = 0
    val top = 0
    val bottom = 0

    val maxRadius = width.coerceAtMost(height) / 2f

    var topLeftCornerRadius = cornerRadius.value * density
        .coerceAtMost(maxRadius)
    var topRightCornerRadius = cornerRadius.value * density
        .coerceAtMost(maxRadius)
    var bottomLeftCornerRadius = cornerRadius.value * density
        .coerceAtMost(maxRadius)
    var bottomRightCornerRadius = cornerRadius.value * density
        .coerceAtMost(maxRadius)

    val arrowTop = 0.0f
    val arrowBottom = 0.0f
    val arrowLeft = 0.0f
    val arrowRight = 0.0f

    when (alignment) {
        // Arrow on left side of the bubble
        ArrowAlignment.LeftTop -> {
            topLeftCornerRadius = min(arrowTop, topLeftCornerRadius)
            bottomLeftCornerRadius =
                min(bottomLeftCornerRadius, (height - arrowBottom))
        }

        // Arrow on right side of the bubble
        ArrowAlignment.RightTop -> {
            topRightCornerRadius = min(arrowTop, topRightCornerRadius)
            bottomRightCornerRadius =
                min(bottomRightCornerRadius, (height - arrowBottom))
        }

        else -> Unit
    }

    addRoundRect(
        RoundRect(
            rect = Rect(0.0f, 0.0f, width, height),
            topLeft = CornerRadius(
                topLeftCornerRadius,
                topLeftCornerRadius,
            ),
            topRight = CornerRadius(
                topRightCornerRadius,
                topRightCornerRadius,
            ),
            bottomRight = CornerRadius(
                bottomRightCornerRadius,
                bottomRightCornerRadius,
            ),
            bottomLeft = CornerRadius(
                bottomLeftCornerRadius,
                bottomLeftCornerRadius,
            ),
        ),
    )
    if (alignment == ArrowAlignment.LeftTop) {
        moveTo(0.0f, arrowTop)

        lineTo(-(10.dp.value * density), arrowTop)
        lineTo(0.0f, (10.dp.value * density))
    } else if (alignment == ArrowAlignment.RightTop) {
        moveTo(width, arrowTop)

        lineTo(width + (10.dp.value * density), arrowTop)
        lineTo(width, +(10.dp.value * density))
    }
}

enum class ArrowShape {
    HalfTriangle,
    FullTriangle,
    Curved,
}

enum class ArrowAlignment {
    None,
    LeftTop,
    RightTop,
}
