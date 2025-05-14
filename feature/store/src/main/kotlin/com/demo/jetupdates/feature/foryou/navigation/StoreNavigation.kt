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

package com.demo.jetupdates.feature.foryou.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.demo.jetupdates.feature.foryou.StoreScreenRoute
import kotlinx.serialization.Serializable

@Serializable data object StoreRoute // route to ForYou screen

@Serializable data object StoreBaseRoute // route to base navigation graph

fun NavController.navigateToStore(navOptions: NavOptions) = navigate(route = StoreRoute, navOptions)

/**
 *  The ForYou section of the app. It can also display information about topics.
 *  This should be supplied from a separate module.
 *
 *  @param onTopicClick - Called when a topic is clicked, contains the ID of the topic
 *  @param topicDestination - Destination for topic content
 */
fun NavGraphBuilder.storeSection(
    onItemClick: (String) -> Unit,
) {
    navigation<StoreBaseRoute>(startDestination = StoreRoute) {
        composable<StoreRoute>{
            StoreScreenRoute(onTopicClick = onItemClick)
        }
        //topicDestination()
    }
}
