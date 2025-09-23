/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("ktlint:standard:max-line-length")

package com.demo.jetupdates.core.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.demo.jetupdates.core.designsystem.R.drawable
import com.demo.jetupdates.core.model.data.Message
import com.demo.jetupdates.core.ui.EMOJIS.EMOJI_PINK_HEART
import com.demo.jetupdates.core.ui.MessagePreviewParameterData.initialMessages

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [Message] for Composable previews.
 */
class MessagePreviewParameterProvider :
    PreviewParameterProvider<List<Message>> {
    override val values: Sequence<List<Message>>
        get() = sequenceOf(initialMessages)
}

object MessagePreviewParameterData {
    val initialMessages = listOf(

        Message(
            "1",
            "me",
            "Thank you!$EMOJI_PINK_HEART«",
            "8:06 PM",
            drawable.core_designsystem_avtar,
        ),

        Message(
            "2",
            "me",
            "Maybe I will \uD83D\uDE04\n" +
                "Only if you promise not to roast them \uD83D\uDE1C",
            "8:06 PM",
            drawable.core_designsystem_avtar,
        ),

        Message(
            "3",
            "Rahul Dev",
            "That’s so cool! \uD83D\uDE0D\n" +
                "You’ve always been good at art!\n" +
                "You should share some of your work sometime \uD83D\uDC40",
            "8:12 PM",
            drawable.core_designsystem_someone_else,
        ),

        Message(
            "4",
            "me",
            "Kinda! I started sketching again \uD83C\uDFA8✍\uFE0F\n" +
                "Nothing fancy… just doodles and random stuff\n" +
                "It’s helping me stay sane \uD83D\uDE02",
            "8:10 PM",
            drawable.core_designsystem_avtar,
        ),

        Message(
            "5",
            "Rahul Dev",
            "Barely \uD83D\uDE15\n" +
                "Tried to start reading again… but I just keep falling asleep on the first page \uD83D\uDE34\uD83D\uDCD6\n" +
                "What about you? Doing anything for fun?",
            "8:08 PM",
            drawable.core_designsystem_someone_else,
        ),

        Message(
            "6",
            "me",
            "LOL relatable \uD83D\uDE02\n" +
                "But hey, at least we’re surviving!\n" +
                "Have you been taking any breaks? Like actual “me time”? \uD83E\uDDD8\u200D♂\uFE0F",
            "8:03 PM",
            drawable.core_designsystem_avtar,
        ),
        Message(
            "7",
            "Rahul Dev",
            "Haha exactly!! \uD83D\uDE02\n" +
                "Sometimes I just want to throw my laptop out the window \uD83D\uDEAA\uD83D\uDCBB\n" +
                "But then I remember… EMI \uD83D\uDE02",
            "8:04 PM",
            drawable.core_designsystem_someone_else,
        ),
        Message(
            "8",
            "me",
            "Ugh I get that \uD83D\uDCAF\n" +
                "Same here!\n" +
                "This week especially feels like a whole month \uD83D\uDE35\u200D\uD83D\uDCAB\n" +
                "Everyone’s acting like there are 48 hours in a day!",
            "8:05 PM",
            drawable.core_designsystem_avtar,
        ),
        Message(
            "9",
            "Rahul Dev",
            "Hey Priya! \uD83D\uDE04\n" +
                "Tell me about it! Been crazy busy lately \uD83D\uDE48\n" +
                "Work has been non-stop… deadlines, meetings, and more deadlines ",
            "8:05 PM",
            drawable.core_designsystem_someone_else,
        ),
        Message(
            "10",
            "me",
            "Hey Rahul! \uD83D\uDE0A\n" +
                "Long time no proper chat \uD83D\uDE05\n" +
                "How’s life treating you these days?",
            "8:07 PM",
            drawable.core_designsystem_avtar,
        ),

    )
}

object EMOJIS {
    // EMOJI 15
    const val EMOJI_PINK_HEART = "\uD83E\uDE77"

    // EMOJI 14 🫠
    const val EMOJI_MELTING = "\uD83E\uDEE0"

    // ANDROID 13.1 😶‍🌫️
    const val EMOJI_CLOUDS = "\uD83D\uDE36\u200D\uD83C\uDF2B️"

    // ANDROID 12.0 🦩
    const val EMOJI_FLAMINGO = "\uD83E\uDDA9"

    // ANDROID 12.0  👉
    const val EMOJI_POINTS = " \uD83D\uDC49"
}
