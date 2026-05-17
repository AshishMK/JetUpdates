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

package com.demo.jetupdates.feature.chat.impl.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.demo.jetupdates.core.navigation.Navigator
import com.demo.jetupdates.core.ui.LocalNavAnimatedVisibilityScope
import com.demo.jetupdates.core.ui.LocalSnackbarHostState
import com.demo.jetupdates.feature.chat.api.navigation.ChatNavKey
import com.demo.jetupdates.feature.chat.impl.ChatScreen

fun EntryProviderScope<NavKey>.chatEntry(navigator: Navigator) {
    entry<ChatNavKey> {
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides LocalNavAnimatedContentScope.current,
        ) {
            val snackbarHostState = LocalSnackbarHostState.current
            ChatScreen(
                onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action,
                        duration = SnackbarDuration.Short,
                    ) == ActionPerformed
                },
                onBack = { navigator.goBack() },
            )
        }
    }
}
