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

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.jetupdates.core.model.data.Message
import com.demo.jetupdates.core.network.BuildConfig
import com.demo.jetupdates.core.ui.MessagePreviewParameterData.initialMessages
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {
    val exampleUiState = ConversationUiState(
        initialMessages = initialMessages,
    )

    var isGeminiReplying by mutableStateOf(false)

    private var generativeModel: GenerativeModel

    init {
        val config = generationConfig {
            temperature = 0.7f
        }
        Log.v("chatvv", "chatvv ${BuildConfig.API_KEY}")
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash-latest",
            apiKey = BuildConfig.API_KEY,
            generationConfig = config,
        )
    }

    fun summarize(prompt: String) {
        viewModelScope.launch {
            isGeminiReplying = true
            val message = generativeModel.generateContent(prompt).text?.trim() ?: "Not Found"
            isGeminiReplying = false
            exampleUiState.addMessage(
                Message(
                    "Gemini",
                    message,
                    "9:00",
                    com.demo.jetupdates.core.designsystem.R.drawable.core_designsystem_gemini,
                ),
            )
        }
    }
}

class ConversationUiState(
    initialMessages: List<Message>,
) {
    private val _messages: MutableList<Message> = initialMessages.toMutableStateList()
    val messages: List<Message> = _messages

    fun addMessage(msg: Message) {
        _messages.add(0, msg) // Add to the beginning of the list
    }
}
