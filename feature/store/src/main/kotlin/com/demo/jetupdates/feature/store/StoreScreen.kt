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

package com.demo.jetupdates.feature.store

import android.content.res.Configuration
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.jetupdates.core.designsystem.component.AppBackground
import com.demo.jetupdates.core.designsystem.component.AppButton
import com.demo.jetupdates.core.designsystem.component.AppOverlayLoadingWheel
import com.demo.jetupdates.core.designsystem.component.DynamicAsyncImage
import com.demo.jetupdates.core.designsystem.component.scrollbar.DraggableScrollbar
import com.demo.jetupdates.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.demo.jetupdates.core.designsystem.component.scrollbar.scrollbarState
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.core.ui.DevicePreviews
import com.demo.jetupdates.core.ui.ItemFeedUiState
import com.demo.jetupdates.core.ui.TrackScrollJank
import com.demo.jetupdates.core.ui.UserShopResourcePreviewParameterProvider
import com.demo.jetupdates.core.ui.itemFeed
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus.Denied
import com.google.accompanist.permissions.rememberPermissionState

//
@Composable
internal fun StoreScreenRoute(
    onCategoryClick: (Int) -> Unit,
    showCategoryList: Boolean,
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel = hiltViewModel(),
) {
    val onboardingUiState by viewModel.onboardingUiState.collectAsStateWithLifecycle()
    val feedState by viewModel.feedState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val deepLinkedUserShopItem by viewModel.deepLinkedShopItem.collectAsStateWithLifecycle()

    StoreScreen(
        showCategoryList = showCategoryList,
        isSyncing = isSyncing,
        onboardingUiState = onboardingUiState,
        feedState = feedState,
        deepLinkedUserShopItem = deepLinkedUserShopItem,
        onCategoryCheckedChanged = viewModel::updateCategorySelection,
        onDeepLinkOpened = viewModel::onDeepLinkOpened,
        onCategoryClick = onCategoryClick,
        saveFollowedCategories = viewModel::dismissOnboarding,
        categoryActionClicked = viewModel::categoryActionClicked,
        onShopItemCheckedChanged = viewModel::updateShopItemSaved,
        onShopItemViewed = { viewModel.setShopItemViewed(it, true) },
        modifier = modifier,
    )
}

@Composable
internal fun StoreScreen(
    showCategoryList: Boolean,
    isSyncing: Boolean,
    onboardingUiState: OnboardingUiState,
    feedState: ItemFeedUiState,
    deepLinkedUserShopItem: UserShopItem?,
    onCategoryCheckedChanged: (Int, Boolean) -> Unit,
    onCategoryClick: (Int) -> Unit,
    onDeepLinkOpened: (Int) -> Unit,
    saveFollowedCategories: () -> Unit,
    categoryActionClicked: (Boolean) -> Unit,
    onShopItemCheckedChanged: (Int, Boolean) -> Unit,
    onShopItemViewed: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOnboardingLoading = onboardingUiState is OnboardingUiState.Loading
    val isFeedLoading = feedState is ItemFeedUiState.Loading

    // This code should be called when the UI is ready for use and relates to Time To Full Display.
    ReportDrawnWhen { !isSyncing && !isOnboardingLoading && !isFeedLoading }

    val itemsAvailable = feedItemsSize(feedState, onboardingUiState)

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(
        itemsAvailable = itemsAvailable,
    )

    TrackScrollJank(scrollableState = state, stateName = "store:feed")
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(150.dp),

            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalItemSpacing = 16.dp,
            modifier = Modifier
                .testTag("store:feed"),
            state = state,
        ) {
            onboarding(
                showCategoryList = showCategoryList,
                onboardingUiState = onboardingUiState,
                onCategoryCheckedChanged = onCategoryCheckedChanged,
                saveFollowedCategories = saveFollowedCategories,
                categoryActionClicked = categoryActionClicked,
                // Custom LayoutModifier to remove the enforced parent 16.dp contentPadding
                // from the LazyVerticalGrid and enable edge-to-edge scrolling for this section
                interestsItemModifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        constraints.copy(
                            maxWidth = constraints.maxWidth + 32.dp.roundToPx(),
                        ),
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                },

            )

            itemFeed(
                feedState = feedState,
                onShopItemCheckedChanged = onShopItemCheckedChanged,
                onShopItemViewed = onShopItemViewed,
                onCategoryClick = onCategoryClick,
            )

            item(span = StaggeredGridItemSpan.FullLine, contentType = "bottomSpacing") {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Add space for the content to clear the "offline" snackbar.
                    // TODO: Check that the Scaffold handles this correctly in JUApp
                    // if (isOffline) Spacer(modifier = Modifier.height(48.dp))
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                }
            }
        }

        AnimatedVisibility(
            visible = isSyncing || isFeedLoading || isOnboardingLoading,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
            ) + fadeOut(),
        ) {
            val loadingContentDescription = stringResource(id = R.string.feature_store_loading)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                AppOverlayLoadingWheel(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
            }
        }

        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }

    NotificationPermissionEffect()
    /*  DeepLinkEffect(
          deepLinkedUserShopItem,
          onDeepLinkOpened,
      )*/
}

/**
 * An extension on [LazyListScope] defining the onboarding portion of the for you screen.
 * Depending on the [onboardingUiState], this might emit no items.
 *
 */
private fun LazyStaggeredGridScope.onboarding(
    showCategoryList: Boolean,
    onboardingUiState: OnboardingUiState,
    onCategoryCheckedChanged: (Int, Boolean) -> Unit,
    saveFollowedCategories: () -> Unit,
    categoryActionClicked: (Boolean) -> Unit,
    interestsItemModifier: Modifier = Modifier,
) {
    when (onboardingUiState) {
        OnboardingUiState.Loading,
        OnboardingUiState.LoadFailed,
        -> Unit

        is OnboardingUiState.NotShown -> {
            if (showCategoryList) {
                categoryActionClicked(true)
                return
            }
        }

        is OnboardingUiState.Shown -> {
            if (!showCategoryList) { // if user has manually hide it
                return
            }
            item(span = StaggeredGridItemSpan.FullLine, contentType = "onboarding") {
                Column(modifier = interestsItemModifier) {
                    Text(
                        text = stringResource(R.string.feature_store_onboarding_guidance_title),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.feature_store_onboarding_guidance_subtitle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 24.dp, end = 24.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    ItemSelection(
                        onboardingUiState,
                        onCategoryCheckedChanged,
                        Modifier.padding(bottom = 0.dp),
                    )
                    // Done button
                    if (onboardingUiState.shouldShowOnboarding) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        ) {
                            AppButton(
                                onClick = saveFollowedCategories,
                                enabled = onboardingUiState.isDismissable,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                                    .widthIn(364.dp)
                                    .fillMaxWidth(),
                            ) {
                                Text(
                                    text = stringResource(R.string.feature_store_done),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemSelection(
    onboardingUiState: OnboardingUiState.Shown,
    onCategoryCheckedChanged: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,

) {
    val lazyGridState = rememberLazyStaggeredGridState()
    val categorySelectionTestTag = "store:categorySelection"

    TrackScrollJank(scrollableState = lazyGridState, stateName = categorySelectionTestTag)

    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        LazyHorizontalStaggeredGrid(
            state = lazyGridState,
            rows = StaggeredGridCells.Adaptive(40.dp),
            horizontalItemSpacing = 16.dp,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(24.dp),
            modifier = Modifier
                // LazyHorizontalGrid has to be constrained in height.
                // However, we can't set a fixed height because the horizontal grid contains
                // vertical text that can be rescaled.
                // When the fontScale is at most 1, we know that the horizontal grid will be at most
                // 240dp tall, so this is an upper bound for when the font scale is at most 1.
                // When the fontScale is greater than 1, the height required by the text inside the
                // horizontal grid will increase by at most the same factor, so 240sp is a valid
                // upper bound for how much space we need in that case.
                // The maximum of these two bounds is therefore a valid upper bound in all cases.
                .heightIn(max = max(200.dp, with(LocalDensity.current) { 200.sp.toDp() }))
                .fillMaxWidth()
                .testTag(categorySelectionTestTag),
        ) {
            items(
                items = onboardingUiState.categories,
                key = { it.category.id },
            ) {
                SingleItemButton(
                    name = it.category.name,
                    categoryId = it.category.id,
                    imageUrl = it.category.imageUrl,
                    isSelected = it.isFollowed,
                    onClick = onCategoryCheckedChanged,
                )
            }
        }
    }
}

@Composable
private fun SingleItemButton(
    name: String,
    categoryId: Int,
    imageUrl: String,
    isSelected: Boolean,
    onClick: (Int, Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier
            // .widthIn(min = 100.dp)
            .heightIn(min = 40.dp),
        shape = RoundedCornerShape(corner = CornerSize(24.dp)),
        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
        selected = isSelected,
        onClick = {
            onClick(categoryId, !isSelected)
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp),
        ) {
            ItemIcon(
                imageUrl = imageUrl,
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                // .weight(1f),
                color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun ItemIcon(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    /* Image(
         modifier = modifier
             .padding(0.dp)
             .size(20.dp)
             .clip(CircleShape)                       // clip to the circle shape
             .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),

         contentScale = ContentScale.Crop,
         painter = painterResource(drawable.core_designsystem_ic_placeholder_default),
         contentDescription = null,
         colorFilter = null,
     )*/
    DynamicAsyncImage(
        placeholder = painterResource(R.drawable.feature_store_ic_icon_placeholder),
        imageUrl = imageUrl,
        // decorative
        contentDescription = null,
        modifier = modifier
            .padding(10.dp)
            .size(20.dp),
    )
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun NotificationPermissionEffect() {
    // Permission requests should only be made from an Activity Context, which is not present
    // in previews
    if (LocalInspectionMode.current) return
    if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) return
    val notificationsPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )
    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        if (status is Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}

@Composable
private fun DeepLinkEffect(
    shopItemResource: UserShopItem?,
    onDeepLinkOpened: (Int) -> Unit,
) {
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    LaunchedEffect(shopItemResource) {
        if (shopItemResource == null) return@LaunchedEffect
        if (!shopItemResource.hasBeenViewed) onDeepLinkOpened(shopItemResource.id)

     /*   launchCustomChromeTab(
            context = context,
            uri = Uri.parse(userShopItem.url),
            toolbarColor = backgroundColor,
        )*/
    }
}

private fun feedItemsSize(
    feedState: ItemFeedUiState,
    onboardingUiState: OnboardingUiState,
): Int {
    val feedSize = when (feedState) {
        ItemFeedUiState.Loading -> 0
        is ItemFeedUiState.Success -> feedState.feed.size
    }
    val onboardingSize = when (onboardingUiState) {
        OnboardingUiState.Loading,
        OnboardingUiState.LoadFailed,
        OnboardingUiState.NotShown,
        -> 0

        is OnboardingUiState.Shown -> 1
    }
    return feedSize + onboardingSize
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light theme")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark theme")
annotation class ThemePreviews

@ThemePreviews
@Composable
fun SingleItemButtonPreview() {
    AppTheme {
        AppBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            SingleItemButton(
                name = "Headlines",
                categoryId = 1,
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
                isSelected = false,
                onClick = { _, _ -> },
            )
        }
    }
}

@ThemePreviews
@Composable
fun SingleItemButtonSelectedPreview() {
    AppTheme {
        AppBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            SingleItemButton(
                name = "Headlines",
                categoryId = 1,
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
                isSelected = true,
                onClick = { _, _ -> },
            )
        }
    }
}

@DevicePreviews
@Composable
fun StoreScreenPopulatedFeed(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userShopItems: List<UserShopItem>,
) {
    AppTheme {
        StoreScreen(
            isSyncing = false,
            showCategoryList = false,
            onboardingUiState = OnboardingUiState.NotShown,
            feedState = ItemFeedUiState.Success(
                feed = userShopItems,
            ),
            deepLinkedUserShopItem = null,
            onCategoryCheckedChanged = { _, _ -> },
            saveFollowedCategories = {},
            onShopItemCheckedChanged = { _, _ -> },
            onShopItemViewed = {},
            onCategoryClick = {},
            onDeepLinkOpened = {},
            categoryActionClicked = {},
        )
    }
}

@DevicePreviews
@Composable
fun StoreScreenOfflinePopulatedFeed(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userShopItems: List<UserShopItem>,
) {
    AppTheme {
        StoreScreen(
            isSyncing = false,
            showCategoryList = false,
            onboardingUiState = OnboardingUiState.NotShown,
            feedState = ItemFeedUiState.Success(
                feed = userShopItems,
            ),
            deepLinkedUserShopItem = null,
            onCategoryCheckedChanged = { _, _ -> },
            saveFollowedCategories = {},
            onShopItemCheckedChanged = { _, _ -> },
            onShopItemViewed = {},
            onCategoryClick = {},
            onDeepLinkOpened = {},
            categoryActionClicked = {},
        )
    }
}

@DevicePreviews
@Composable
fun StoreScreenCategorySelection(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userShopItems: List<UserShopItem>,
) {
    AppTheme {
        StoreScreen(
            isSyncing = false,
            showCategoryList = true,
            onboardingUiState = OnboardingUiState.Shown(
                categories = userShopItems.flatMap { shopItem -> shopItem.followableCategories }
                    .distinctBy { it.category.id },
                true,
            ),
            feedState = ItemFeedUiState.Success(
                feed = userShopItems,
            ),
            deepLinkedUserShopItem = null,
            onCategoryCheckedChanged = { _, _ -> },
            saveFollowedCategories = {},
            onShopItemCheckedChanged = { _, _ -> },
            onShopItemViewed = {},
            onCategoryClick = {},
            onDeepLinkOpened = {},
            categoryActionClicked = {},
        )
    }
}

@DevicePreviews
@Composable
fun StoreScreenLoading() {
    AppTheme {
        StoreScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.Loading,
            feedState = ItemFeedUiState.Loading,
            showCategoryList = false,
            deepLinkedUserShopItem = null,
            onCategoryCheckedChanged = { _, _ -> },
            saveFollowedCategories = {},
            onShopItemCheckedChanged = { _, _ -> },
            onShopItemViewed = {},
            onCategoryClick = {},
            onDeepLinkOpened = {},
            categoryActionClicked = {},
        )
    }
}

@DevicePreviews
@Composable
fun StoreScreenPopulatedAndLoading(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userShopItems: List<UserShopItem>,
) {
    AppTheme {
        StoreScreen(
            isSyncing = true,
            onboardingUiState = OnboardingUiState.Loading,
            feedState = ItemFeedUiState.Success(
                feed = userShopItems,
            ),
            deepLinkedUserShopItem = null,
            onCategoryCheckedChanged = { _, _ -> },
            saveFollowedCategories = {},
            onShopItemCheckedChanged = { _, _ -> },
            onShopItemViewed = {},
            onCategoryClick = {},
            onDeepLinkOpened = {},
            categoryActionClicked = {},
            showCategoryList = false,
        )
    }
}
