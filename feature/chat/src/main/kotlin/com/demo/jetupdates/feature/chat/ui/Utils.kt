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

package com.demo.jetupdates.feature.chat.ui

import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Measure text properties and update [chatRowData] based on text width, and line count.
 * Use this method to update if [TextLayoutResult] is not available in overloaded chat row
 * composable.
 */
fun measureText(chatRowData: ChatRowData, textLayoutResult: TextLayoutResult) {
    chatRowData.text = textLayoutResult.layoutInput.text.text
    chatRowData.lineCount = textLayoutResult.lineCount
    chatRowData.lastLineWidth =
        textLayoutResult.getLineRight(chatRowData.lineCount - 1)
    chatRowData.textWidth = textLayoutResult.size.width
}

/**
 * Calculate [message] and [status] positions based on [chatRowData].
 * Number of message text lines, last line width, width of the parent based on quote,
 * user name, images, videos,etc.
 * effect the position of [status] if it's not null
 */
internal fun calculateChatWidthAndHeight(
    chatRowData: ChatRowData,
    name: Placeable?,
    message: Placeable,
    status: Placeable?,
    density: Float,
) {
    val nameHeight = if (name != null) (name.measuredHeight - (2.dp.value * density)).toInt() else 0
    if (status != null) {
        val lineCount = chatRowData.lineCount
        val lastLineWidth = chatRowData.lastLineWidth
        val parentWidth = chatRowData.parentWidth

        val padding = (message.measuredWidth - chatRowData.textWidth) / 2

        // Multiple lines and last line and status is longer than text size and right padding
        if (lineCount > 1 && lastLineWidth + status.measuredWidth >= chatRowData.textWidth + padding) {
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = nameHeight + message.measuredHeight + 40.sp.value.toInt() // status.measuredHeight
            chatRowData.measuredType = 0
        } else if (lineCount > 1 && lastLineWidth + status.measuredWidth < chatRowData.textWidth + padding) {
            // Multiple lines and last line and status is shorter than text size and right padding
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = nameHeight + message.measuredHeight
            chatRowData.measuredType = 1
        } else if (lineCount == 1 && message.width + status.measuredWidth >= parentWidth) {
            chatRowData.rowWidth = message.measuredWidth
            chatRowData.rowHeight = nameHeight + message.measuredHeight + 40.sp.value.toInt() // + status.measuredHeight
            chatRowData.measuredType = 2
        } else {
            chatRowData.rowWidth = message.measuredWidth + status.measuredWidth
            chatRowData.rowHeight = nameHeight + message.measuredHeight
            chatRowData.measuredType = 3
        }
    } else {
        chatRowData.rowWidth = message.width
        chatRowData.rowHeight = nameHeight + message.height
    }
}
