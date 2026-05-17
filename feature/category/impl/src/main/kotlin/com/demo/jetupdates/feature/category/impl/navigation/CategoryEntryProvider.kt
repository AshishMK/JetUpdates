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

package com.demo.jetupdates.feature.category.impl.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.demo.jetupdates.core.navigation.Navigator
import com.demo.jetupdates.core.ui.LocalNavAnimatedVisibilityScope
import com.demo.jetupdates.feature.category.api.navigation.CategoryNavKey
import com.demo.jetupdates.feature.category.impl.CategoryScreen
import com.demo.jetupdates.feature.category.impl.CategoryViewModel
import com.demo.jetupdates.feature.product.api.navigation.navigateToProduct

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.categoryEntry(navigator: Navigator) {
    entry<CategoryNavKey>(
        metadata = ListDetailSceneStrategy.detailPane(),
    ) { key ->
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides LocalNavAnimatedContentScope.current,
        ) {
            val id = key.id
            CategoryScreen(
                showBackButton = true,
                onBackClick = { navigator.goBack() },
                onProductClick = navigator::navigateToProduct,
                viewModel = hiltViewModel<CategoryViewModel, CategoryViewModel.Factory>(
                    key = id.toString(),
                ) { factory ->
                    factory.create(id)
                },
            )
        }
    }
}
