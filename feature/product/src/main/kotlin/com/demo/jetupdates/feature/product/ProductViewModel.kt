/*
 * Copyright 2025 The Android Open Source Project
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

package com.demo.jetupdates.feature.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.demo.jetupdates.core.data.repository.UserDataRepository
import com.demo.jetupdates.core.data.repository.UserShopItemRepository
import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.feature.product.navigation.ProductRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    userShopItemRepository: UserShopItemRepository,
    private val userDataRepository: UserDataRepository,

) : ViewModel() {

    val route = savedStateHandle.toRoute<ProductRoute>()
    var shopItemId: Int = route.initialProductId

    val productUiState: StateFlow<ProductUiState> =
        userShopItemRepository.observeItem(route.initialProductId)
            .map<UserShopItem, ProductUiState>(ProductUiState::Success)
            .onStart { emit(ProductUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProductUiState.Loading,
            )

    fun bookmarkItem(addToCart: Boolean) {
        viewModelScope.launch {
            userDataRepository.setShopItemBookmarked(shopItemId, addToCart)
        }
    }
}

/**
 * A sealed hierarchy describing the state of the product
 */
sealed interface ProductUiState {
    /**
     * The feed is still loading.
     */
    data object Loading : ProductUiState

    /**
     */
    data class Success(
        /**
         * The lProduct for given product Id.
         */
        val product: UserShopItem,
    ) : ProductUiState
}
