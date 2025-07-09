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

package com.demo.jetupdates.feature.product.navigation

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.demo.jetupdates.core.ui.LocalNavAnimatedVisibilityScope
import com.demo.jetupdates.feature.product.ProductRoute
import kotlinx.serialization.Serializable

@Serializable data class ProductRoute(val initialProductId: Int)

fun NavController.navigateToProduct(initialProductId: Int, navOptions: NavOptions? = null) =
    navigate(route = ProductRoute(initialProductId), navOptions)

fun NavGraphBuilder.productScreen(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    onBackClick: () -> Unit,
) {
    composable<ProductRoute> {
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides this@composable,
        ) {
            ProductRoute(windowAdaptiveInfo = windowAdaptiveInfo, onBackClick = onBackClick)
        }
    }
}
