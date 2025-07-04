/*
 * Copyright 2022 The Android Open Source Project
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

package com.demo.jetupdates.feature.chat

import android.content.ClipDescription
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.demo.jetupdates.core.designsystem.component.AppBackground
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.model.data.Message
import com.demo.jetupdates.core.ui.ArrowAlignment
import com.demo.jetupdates.core.ui.DevicePreviews
import com.demo.jetupdates.core.ui.JumpToBottom
import com.demo.jetupdates.core.ui.MessagePreviewParameterData.initialMessages
import com.demo.jetupdates.core.ui.TrackScrollJank
import com.demo.jetupdates.core.ui.UserInput
import com.demo.jetupdates.core.ui.addRoundedBubbleRect
import com.demo.jetupdates.feature.chat.ui.ChatFlexBoxLayout
import kotlinx.coroutines.launch

@Composable
internal fun ChatRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val feedState =
        viewModel.exampleUiState // initialMessages.plus(initialMessages).plus(initialMessages) // for benchmarking
    ChatScreen(
        feedState = feedState,
        onShowSnackbar = onShowSnackbar,
        modifier = modifier,
        ask = { viewModel.summarize(it) },
        geminiInProgress = viewModel.isGeminiReplying,
    )
}

/**
 * Displays the user's bookmarked articles. Includes support for loading and empty states.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
@Composable
internal fun ChatScreen(
    feedState: ConversationUiState,
    ask: (ask: String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    geminiInProgress: Boolean,
) {
    val authorMe = stringResource(R.string.feature_chat_author_me)
    val timeNow = stringResource(R.string.feature_chat_now)
    var test by rememberSaveable { mutableStateOf(false) }
    var hideKeyBoard by rememberSaveable { mutableStateOf(false) }

    if (test) {
        BackHandler(
            onBack = {
                hideKeyBoard = true
                test = false
            },
        )
        // test = false
    }
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dragAndDropCallback = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val clipData = event.toAndroidDragEvent().clipData

                if (clipData.itemCount < 1) {
                    return false
                }

                feedState.addMessage(
                    Message(
                        authorMe,
                        clipData.getItemAt(0).text.toString(),
                        timeNow,
                        com.demo.jetupdates.core.designsystem.R.drawable.core_designsystem_avtar,
                    ),
                )
                ask(clipData.getItemAt(0).text.toString())
                return true
            }
        }
    }

    Column(
        modifier
            .imeOffset(0)
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event
                        .mimeTypes()
                        .contains(
                            ClipDescription.MIMETYPE_TEXT_PLAIN,
                        )
                },
                target = dragAndDropCallback,
            ),
    ) {
        Messages(
            feedState = feedState,
            modifier = Modifier.weight(1f),
            scrollState = scrollState,
        )
        UserInput(
            hideKeyBoard = hideKeyBoard,
            backPressed = {
                test = true
            },
            onMessageSent = { content ->
                feedState.addMessage(
                    Message(
                        authorMe,
                        content,
                        timeNow,
                        com.demo.jetupdates.core.designsystem.R.drawable.core_designsystem_avtar,
                    ),
                )
                ask(content)
            },
            resetScroll = {
                scope.launch {
                    scrollState.scrollToItem(0)
                }
            },
            geminiInProgress = geminiInProgress,
            // let this element handle the padding so that the elevation is shown behind the
            // navigation bar
            // modifier = Modifier.consumeWindowInsets(PaddingValues(bottom = 800.dp))
        )
    }
}

const val CONVERSATION_TEST_TAG = "chat:messages"

@Composable
fun Messages(
    feedState: ConversationUiState,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    TrackScrollJank(scrollableState = scrollState, stateName = CONVERSATION_TEST_TAG)
    Box(modifier = modifier) {
        val authorMe = stringResource(id = R.string.feature_chat_author_me)
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            modifier = Modifier
                .testTag(CONVERSATION_TEST_TAG)
                .fillMaxSize(),
        ) {
            val messages = feedState.messages
            for (index in messages.indices) {
                Log.v("recomposing", "recomposing ccc $index")
                val prevAuthor = messages.getOrNull(index - 1)?.author
                val nextAuthor = messages.getOrNull(index + 1)?.author
                val content = messages[index]
                val isFirstMessageByAuthor = prevAuthor != content.author
                val isLastMessageByAuthor = nextAuthor != content.author

                // Hardcode day dividers for simplicity
                if (index == messages.size - 1) {
                    item {
                        DayHeader("20 Aug")
                    }
                } else if (index == 2) {
                    item {
                        DayHeader("Today")
                    }
                }

                item {
                    Message(
                        onAuthorClick = { name -> },
                        msg = content,
                        isUserMe = content.author == authorMe,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor,
                    )
                }
            }
        }
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        // Show the button if the first visible item is not the first one or if the offset is
        // greater than the threshold.
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                    scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun Message(
    onAuthorClick: (String) -> Unit,
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
) {
    val borderColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier
    if (isUserMe) {
        Row(modifier = spaceBetweenAuthors, horizontalArrangement = Arrangement.End) {
            AuthorAndTextMessage(
                msg = msg,
                isUserMe = isUserMe,
                isFirstMessageByAuthor = isFirstMessageByAuthor,
                isLastMessageByAuthor = isLastMessageByAuthor,
                authorClicked = onAuthorClick,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
            )
            if (isLastMessageByAuthor) {
                // Avatar
                Image(
                    modifier = Modifier
                        .clickable(onClick = { onAuthorClick(msg.author) })
                        .padding(horizontal = 16.dp)
                        .size(42.dp)
                        .border(1.5.dp, borderColor, CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .clip(CircleShape)
                        .align(Alignment.Top),

                    painter = painterResource(id = msg.authorImage),
                    contentScale = ContentScale.Crop,
                    contentDescription = msg.content,
                )
            } else {
                // Space under avatar
                Spacer(modifier = Modifier.width(74.dp))
            }
        }
    } else {
        Row(modifier = spaceBetweenAuthors) {
            if (isLastMessageByAuthor) {
                // Avatar
                Image(
                    modifier = Modifier
                        .clickable(onClick = { onAuthorClick(msg.author) })
                        .padding(horizontal = 16.dp)
                        .size(42.dp)
                        .border(1.5.dp, borderColor, CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .clip(CircleShape)
                        .align(Alignment.Top),

                    painter = painterResource(id = msg.authorImage),
                    contentScale = ContentScale.Crop,
                    contentDescription = msg.content,
                )
            } else {
                // Space under avatar
                Spacer(modifier = Modifier.width(74.dp))
            }
            AuthorAndTextMessage(
                msg = msg,
                isUserMe = isUserMe,
                isFirstMessageByAuthor = isFirstMessageByAuthor,
                isLastMessageByAuthor = isLastMessageByAuthor,
                authorClicked = onAuthorClick,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(1f),
            )
        }
    }
}

@Composable
fun AuthorAndTextMessage(
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isUserMe) Alignment.End else Alignment.Start,
    ) {
        if (isLastMessageByAuthor) {
            // AuthorNameTimestamp(msg)
        }
        ChatItemBubble(
            msg,
            isUserMe,
            authorClicked = authorClicked,
            isLastMessageByAuthor = isLastMessageByAuthor,
        )
        if (isFirstMessageByAuthor) {
            // Last bubble before next author
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            // Between bubbles
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun AuthorNameTimestamp(msg: Message) {
    // Combine author and timestamp for a11y.
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = msg.author,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .alignBy(LastBaseline)
                .paddingFrom(LastBaseline, after = 8.dp), // Space to 1st bubble
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = msg.timestamp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp),
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    HorizontalDivider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    )
}

fun getBubbleType(
    isUserMe: Boolean,
    isLastMessageByAuthor: Boolean,
): ArrowAlignment {
    return if (!isLastMessageByAuthor) {
        ArrowAlignment.None
    } else if (isUserMe) {
        ArrowAlignment.RightTop
    } else {
        ArrowAlignment.LeftTop
    }
}

@Composable
fun ChatItemBubble(
    message: Message,
    isUserMe: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
) {
    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    val den = LocalDensity.current.density
    Column(
        modifier = Modifier.drawWithCache {
            onDrawBehind {
                // drawRect(color = Color.Red,size = size )
                val p = Path()
                p.addRoundedBubbleRect(
                    contentRect = size,
                    density = den,
                    alignment = getBubbleType(isUserMe, isLastMessageByAuthor),
                )
                drawPath(
                    p,
                    color = backgroundBubbleColor,

                    style = Fill,
                )
            }
        },
    ) {
        /*  Surface(
              color = backgroundBubbleColor,
              shape = ChatBubbleShape,
          ) {*/
        SentMessageRow(message = message, isLastMessageByAuthor)
        /*     ClickableMessage(
                 message = message,
             )*/
        // }
    }
}

@Composable
fun ClickableMessage(
    message: Message,
) {
    Text(
        text = message.content,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        modifier = Modifier
            .padding(8.dp)
            .clickable { },
    )
}

private val JumpToBottomThreshold = 56.dp

fun Modifier.imeOffset(contentBottom: Int = 0) = composed {
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)

    /*   val navBarBottom = WindowInsets.navigationBars.getBottom(density)
       Log.v("hawaii", "hawaii $navBarBottom $density")*/
    Modifier.offset {
        IntOffset(
            x = 0,
            y = (if (imeBottom == 0) 0 else -64) // almost 24.33 without density multiplication i.e 2.63
                .coerceAtMost(0),
        )
    }
}

@DevicePreviews
@Composable
fun ChatScreenPreview() {
    AppTheme {
        AppBackground {
            ChatScreen(
                feedState = ConversationUiState(initialMessages),
                onShowSnackbar = { _, _ -> true },
                ask = {},
                geminiInProgress = false,
            )
        }
    }
}

class TriangleEdgeShape(val offset: Int) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val trianglePath = Path().apply {
            moveTo(x = 0f, y = size.height - offset)
            lineTo(x = 0f, y = size.height)
            lineTo(x = 0f + offset, y = size.height)
        }
        return Outline.Generic(path = trianglePath)
    }
}

@Composable
private fun SentMessageRow(
    message: Message,
    isLastMessageByAuthor: Boolean,
) {
    /* Column(
         modifier = Modifier
             .padding(start = 60.dp, end = 8.dp, top = if (drawArrow) 2.dp else 0.dp, bottom = 2.dp)
             .fillMaxWidth()
             .wrapContentHeight(),
         horizontalAlignment = Alignment.End,
     ) {*/

    ChatFlexBoxLayout(
        modifier = Modifier
            .padding(start = 4.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
        text = message.content,
        author = message.author,
        messageStat = {
            MessageTimeText(
                modifier = Modifier.wrapContentSize(),
                messageTime = message.timestamp,
            )
        },
        isLastMessageByAuthor = isLastMessageByAuthor,
    )

    // }
}

/*@Composable
private fun ReceivedMessageRow(
    drawArrow: Boolean = true,
    text: String,
    messageTime: String
) {

    Column(
        modifier = Modifier
            .padding(start = 8.dp, end = 60.dp, top = if (drawArrow) 2.dp else 0.dp, bottom = 2.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start
    ) {
        BubbleLayout(
            bubbleState = rememberBubbleState(
                alignment = ArrowAlignment.LeftTop,
                drawArrow = drawArrow,
                cornerRadius = 8.dp,
            ),
            shadow = BubbleShadow(elevation = 1.dp)
        ) {
            ChatFlexBoxLayout(
                modifier = Modifier
                    .padding(start = 2.dp, top = 2.dp, end = 4.dp, bottom = 2.dp),
                text = text,
                messageStat = {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            modifier = Modifier.padding(top = 1.dp, bottom = 1.dp, end = 4.dp),
                            text = messageTime,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }
    }
}*/

@Composable
fun MessageTimeText(
    modifier: Modifier = Modifier,
    messageTime: String,
) {
    Text(
        modifier = modifier,
        text = messageTime,
        fontSize = 12.sp,
    )
}
