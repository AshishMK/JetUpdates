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

package com.demo.jetupdates.feature.search.impl.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.demo.jetupdates.core.navigation.Navigator
import com.demo.jetupdates.core.ui.LocalNavAnimatedVisibilityScope
import com.demo.jetupdates.feature.category.api.navigation.navigateToCategory
import com.demo.jetupdates.feature.product.api.navigation.navigateToProduct
import com.demo.jetupdates.feature.search.impl.SearchScreen
import com.demo.jetupdates.feature.trending.api.navigation.TrendingNavKey
import com.google.samples.apps.nowinandroid.feature.search.api.navigation.SearchNavKey

fun EntryProviderScope<NavKey>.searchEntry(navigator: Navigator) {
    entry<SearchNavKey> {
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides LocalNavAnimatedContentScope.current,
        ) {
            SearchScreen(
                onBackClick = { navigator.goBack() },
                onTrendingClick = { navigator.navigate(TrendingNavKey()) },
                onCategoryClick = navigator::navigateToCategory,
                onProductClick = navigator::navigateToProduct,
            )
        }
    }
}
