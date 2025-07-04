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

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Message to contain chat context. This is wrapper around [Text] with [AnnotatedString] context.
 */
@Composable
fun MessageText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    author: String,
    isLastMessageByAuthor: Boolean,
    onTextLayout: (TextLayoutResult) -> Unit,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 16.sp,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
) {
    if (isLastMessageByAuthor) {
        Text(
            text = author,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier, // Space to 1st bubble
        )
    }

    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),

        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
    )
    /*    Text(
                modifier = modifier,
                text = text,
                onTextLayout = onTextLayout,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
            )*/
}

/**
 * Message to contain chat context. This is wrapper around [Text] with [String] context.
 */
@Composable
fun MessageText(
    modifier: Modifier = Modifier,
    text: String,
    author: String,
    isLastMessageByAuthor: Boolean,
    onTextLayout: (TextLayoutResult) -> Unit,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 16.sp,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
) {
    if (isLastMessageByAuthor) {
        Text(
            text = author,
            onTextLayout = onTextLayout,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier, // Space to 1st bubble
        )
    }

    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        textAlign = textAlign,
        lineHeight = lineHeight,
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
    )
    /* Text(
          modifier = modifier,
          text = text,
          onTextLayout = onTextLayout,
          color = color,
          fontSize = fontSize,
          fontStyle = fontStyle,
          fontWeight = fontWeight,
          fontFamily = fontFamily,
          letterSpacing = letterSpacing,
          textDecoration = textDecoration,
          textAlign = textAlign,
          lineHeight = lineHeight,
          overflow = overflow,
          softWrap = softWrap,
          maxLines = maxLines,
      )*/
}
