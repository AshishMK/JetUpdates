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

/**
 * Class that contains info about chat message text, textWidth, if line count,
 * width of last line and other attributes.
 *
 * [textWidth], [text]
 */
class ChatRowData {

    var text: String = ""
        internal set

    var author: String = ""
        internal set

    // Width of the text without padding
    var textWidth: Int = 0
        internal set
    var lastLineWidth: Float = 0f
        internal set
    var lineCount: Int = 0
        internal set

    var rowWidth: Int = 0
        internal set

    var rowHeight: Int = 0
        internal set

    var parentWidth: Int = 0
        internal set

    var measuredType: Int = 0
        internal set

    override fun toString(): String {
        return "ChatRowData text: $text, " +
            "lastLineWidth: $lastLineWidth, lineCount: $lineCount, " +
            "textWidth: $textWidth, rowWidth: $rowWidth, height: $rowHeight, " +
            "parentWidth: $parentWidth, measuredType: $measuredType"
    }
}
