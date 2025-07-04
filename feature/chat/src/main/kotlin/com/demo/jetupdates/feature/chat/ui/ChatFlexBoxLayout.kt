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

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Layout that contains message and message status. [messageStat] is positioned based on
 * how many lines [text] has and with of message composable and [messageStat] or parent of
 * this composable. [messageStat] can be position right side or bottom or top of last line
 * of the message.
 *
 * Since [TextLayoutResult] is required for text properties composable contains message but
 * [messageStat] is a parameter of this function which can be created in lambda block.
 *
 * @param modifier [Modifier] of Chat layout that contains message context, time and status
 * @param messageModifier [Modifier] of layout that contains only the message
 * @param text This is the context of the message.
 * @param fontSize of message [Text].
 * @param fontStyle of message [Text].
 * @param fontWeight of message [Text].
 * @param fontFamily of message [Text].
 * @param letterSpacing of message [Text].
 * @param textDecoration of message [Text].
 * @param textAlign of message [Text].
 * @param lineHeight of message [Text].
 * @param overflow of message [Text].
 * @param softWrap of message [Text].
 * @param maxLines of message [Text].
 * @param messageStat composable that might contain message date and message receive status.
 * @param onMeasure returns results from measuring and positioning chat components.
 */
@Composable
fun ChatFlexBoxLayout(
    modifier: Modifier = Modifier,
    messageModifier: Modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
    text: String,
    author: String,
    isLastMessageByAuthor: Boolean,
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
    messageStat: @Composable () -> Unit = {},
    onMeasure: ((ChatRowData) -> Unit)? = null,
) {
    val chatRowData = remember { ChatRowData() }
    val content = @Composable {
        println("👀 ChatFlexBoxLayout OVERLOAD @Composable text: $text")

        MessageText(
            modifier = messageModifier,
            text = text,
            isLastMessageByAuthor = isLastMessageByAuthor,
            author = author,
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
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                chatRowData.text = text
                // maxWidth of text constraint returns parent maxWidth - horizontal padding
                chatRowData.lineCount = textLayoutResult.lineCount
                chatRowData.lastLineWidth =
                    textLayoutResult.getLineRight(chatRowData.lineCount - 1)
                chatRowData.textWidth = textLayoutResult.size.width
            },
        )

        messageStat()
    }

    ChatLayout(modifier, chatRowData, isLastMessageByAuthor, content, onMeasure)
}

/**
 * Layout that contains message and message status. [messageStat] is positioned based on
 * how many lines [text] has and with of message composable and [messageStat] or parent of
 * this composable. [messageStat] can be position right side or bottom or top of last line
 * of the message.
 *
 * Since [TextLayoutResult] is required for text properties composable contains message but
 * [messageStat] is a parameter of this function which can be created in lambda block.
 *
 * @param modifier [Modifier] of Chat layout that contains message context, time and status
 * @param messageModifier [Modifier] of layout that contains only the message
 * @param text This is the context of the message as [AnnotatedString].
 * @param fontSize of message [Text].
 * @param fontStyle of message [Text].
 * @param fontWeight of message [Text].
 * @param fontFamily of message [Text].
 * @param letterSpacing of message [Text].
 * @param textDecoration of message [Text].
 * @param textAlign of message [Text].
 * @param lineHeight of message [Text].
 * @param overflow of message [Text].
 * @param softWrap of message [Text].
 * @param maxLines of message [Text].
 * @param messageStat composable that might contain message date and message receive status.
 * @param onMeasure returns results from measuring and positioning chat components.
 */
@Composable
fun ChatFlexBoxLayout(
    modifier: Modifier,
    messageModifier: Modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
    text: AnnotatedString,
    author: String,
    isLastMessageByAuthor: Boolean,
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
    messageStat: @Composable () -> Unit = {},
    onMeasure: ((ChatRowData) -> Unit)? = null,

) {
    val chatRowData = remember { ChatRowData() }

    val content = @Composable {
        println("🔥 ChatFlexBoxLayout() OVERLOAD2 @Composable text: ${text.text}")

        MessageText(
            modifier = messageModifier,
            isLastMessageByAuthor = isLastMessageByAuthor,
            text = text,
            author = author,
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
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                chatRowData.text = text.text

                // maxWidth of text constraint returns parent maxWidth - horizontal padding
                chatRowData.lineCount = textLayoutResult.lineCount
                chatRowData.lastLineWidth =
                    textLayoutResult.getLineRight(chatRowData.lineCount - 1)
                chatRowData.textWidth = textLayoutResult.size.width
            },
        )
        messageStat()
    }
    ChatLayout(modifier, chatRowData, isLastMessageByAuthor, content, onMeasure)
}

/**
 * Layout that contains message and message status. [message] contains context of message
 * while [messageStat] is optional Composable that stores message date and status.
 *
 * This overload is useful when you need to pass your own composable for **message**
 * instead of using [AnnotatedString] or [String] to existing [Message] composable.
 *
 * ### Usage
 * You should call [measureText] function in [Text]'s
 * **onTextLayout** to update [chatRowData] with text parameters.
 *
 * @param modifier [Modifier] of Chat layout that contains message context, time and status
 * @param message composable
 * @param messageStat can be a Composable that stores message date and message receive status
 * @param chatRowData contains info about chat message text, textWidth, if line count,
 * width of last line and other attributes.
 * @param onMeasure returns results from measuring and positioning chat components.
 *
 */
@Composable
fun ChatFlexBoxLayout(
    modifier: Modifier,
    isLastMessageByAuthor: Boolean,
    message: @Composable () -> Unit,
    messageStat: @Composable () -> Unit = {},
    chatRowData: ChatRowData,
    onMeasure: ((ChatRowData) -> Unit)? = null,
) {
    val content = @Composable {
        println("🤡 ChatFlexBoxLayout() OVERLOAD3 @Composable text: ${chatRowData.text}")

        message()
        messageStat()
    }
    ChatLayout(modifier, chatRowData, isLastMessageByAuthor, content, onMeasure)
}

/**
 * Chat Composable that positions it's contents(message only or message and message status)
 * based on [chatRowData] calculation.
 *
 * @param modifier Modifier for entire layout
 * @param chatRowData contains text and dimension related data about this row
 * @param content contains message or message + message status(time and received status if specified)
 * @param onMeasure lambda to return [chatRowData] contains info about chat message text,
 * textWidth, if line count, width of last line and other attributes.
 */
@Composable
internal fun ChatLayout(
    modifier: Modifier = Modifier,
    chatRowData: ChatRowData,
    isLastMessageByAuthor: Boolean,
    content: @Composable () -> Unit,
    onMeasure: ((ChatRowData) -> Unit)? = null,
) {
    val density = LocalDensity.current.density
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables: List<Measurable>, constraints: Constraints ->

        println("⚠️ ChatLayout() constraints: $constraints")

        val placeables: List<Placeable> = measurables.map { measurable ->
            // Measure each child maximum constraints since message can cover all of the available
            // space by parent
            measurable.measure(Constraints(0, constraints.maxWidth))
        }

        // We can have either only message or
        // message + container with message date + receive status
        require(placeables.size in 1..if (isLastMessageByAuthor) 3 else 2)

        val name = if (isLastMessageByAuthor) placeables.first() else null
        val message = placeables[if (isLastMessageByAuthor) 1 else 0]
        val status = if (placeables.size > if (isLastMessageByAuthor) 2 else 1) {
            placeables.last()
        } else {
            null
        }

        // Constrain with max width instead of longest sibling
        // since this composable can be longest of siblings after calculation
        chatRowData.parentWidth = constraints.maxWidth
        calculateChatWidthAndHeight(chatRowData, name, message, status, density)
        // Parent width of this chat row is either result of width calculation
        // or quote or other sibling width if they are longer than calculated width.
        // minWidth of Constraint equals (text width + horizontal padding)
        chatRowData.parentWidth =
            chatRowData.rowWidth.coerceAtLeast(minimumValue = constraints.minWidth)

        println("⚠️⚠️ ChatLayout() after calculation-> CHAT_ROW_DATA: $chatRowData")

        // Send measurement results if requested by Composable
        onMeasure?.invoke(chatRowData)

        layout(width = chatRowData.parentWidth, height = chatRowData.rowHeight) {
            println(
                "⚠️⚠️⚠️ ChatLayout()-> layout() status x: ${chatRowData.parentWidth - (status?.width ?: 0)}, " +
                    "y: ${chatRowData.rowHeight - (status?.height ?: 0)}\n\n",
            )

            if (isLastMessageByAuthor) {
                name?.placeRelative(0, (2.dp.value * density).toInt())
            }

            message.placeRelative(0, if (name != null) (name.height - (2.dp.value * density)).toInt() else 0)

            // set left of status relative to parent because other elements could result this row
            // to be long as longest composable
            status?.placeRelative(
                chatRowData.parentWidth - status.width,
                chatRowData.rowHeight - status.height,
            )
        }
    }
}
