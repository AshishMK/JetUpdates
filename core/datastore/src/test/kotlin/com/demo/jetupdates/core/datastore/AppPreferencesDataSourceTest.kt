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

package com.demo.jetupdates.core.datastore

import com.demo.jetupdates.core.datastore.test.InMemoryDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppPreferencesDataSourceTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: AppPreferencesDataSource

    @Before
    fun setup() {
        subject = AppPreferencesDataSource(InMemoryDataStore(UserPreferences.getDefaultInstance()))
    }

    @Test
    fun shouldHideOnboardingIsFalseByDefault() = testScope.runTest {
        assertFalse(subject.userData.first().shouldHideOnboarding)
    }

    @Test
    fun userShouldHideOnboardingIsTrueWhenSet() = testScope.runTest {
        subject.setShouldHideOnboarding(true)
        assertTrue(subject.userData.first().shouldHideOnboarding)
    }

    @Test
    fun userShouldHideOnboarding_unfollowsLastCategory_shouldHideOnboardingIsFalse() =
        testScope.runTest {
            // Given: user completes onboarding by selecting a single category.
            subject.setCategoryIdFollowed(1, true)
            subject.setShouldHideOnboarding(true)

            // When: they unfollow that category.
            subject.setCategoryIdFollowed(1, false)

            // Then: onboarding should be shown again
            assertFalse(subject.userData.first().shouldHideOnboarding)
        }

    @Test
    fun userShouldHideOnboarding_unfollowsAllCategories_shouldHideOnboardingIsFalse() =
        testScope.runTest {
            // Given: user completes onboarding by selecting several categories.
            subject.setFollowedCategoryIds(setOf(1, 2))
            subject.setShouldHideOnboarding(true)

            // When: they unfollow those categories.
            subject.setFollowedCategoryIds(emptySet())

            // Then: onboarding should be shown again
            assertFalse(subject.userData.first().shouldHideOnboarding)
        }

    @Test
    fun shouldUseDynamicColorFalseByDefault() = testScope.runTest {
        assertFalse(subject.userData.first().useDynamicColor)
    }

    @Test
    fun userShouldUseDynamicColorIsTrueWhenSet() = testScope.runTest {
        subject.setDynamicColorPreference(true)
        assertTrue(subject.userData.first().useDynamicColor)
    }
}
