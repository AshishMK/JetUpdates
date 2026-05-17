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

package com.demo.jetupdates.feature.trending.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.demo.jetupdates.core.navigation.Navigator
import com.demo.jetupdates.core.ui.LocalNavAnimatedVisibilityScope
import com.demo.jetupdates.feature.category.api.navigation.navigateToCategory
import com.demo.jetupdates.feature.trending.api.navigation.TrendingNavKey
import com.demo.jetupdates.feature.trending.impl.TrendingDetailPlaceholder
import com.demo.jetupdates.feature.trending.impl.TrendingScreen
import com.demo.jetupdates.feature.trending.impl.TrendingViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.trendingEntry(navigator: Navigator) {
    entry<TrendingNavKey>(
        metadata = ListDetailSceneStrategy.listPane {
            TrendingDetailPlaceholder()
        },
    ) { key ->
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides LocalNavAnimatedContentScope.current,
        ) {
            val viewModel = hiltViewModel<TrendingViewModel, TrendingViewModel.Factory> {
                it.create(key)
            }
            TrendingScreen(
                // TODO: This event should either be provided by the ViewModel or by the navigator, not both
                onCategoryClick = navigator::navigateToCategory,

                // TODO: This should be dynamically calculated based on the rendering scene
                //  See https://github.com/android/nav3-recipes/commit/488f4811791ca3ed7192f4fe3c86e7371b32ebdc#diff-374e02026cdd2f68057dd940f203dc4ba7319930b33e9555c61af7e072211cabR89
                shouldHighlightSelectedCategory = false,
                viewModel = viewModel,
            )
        }
    }
}
