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

package com.demo.jetupdates.core.ui

import android.content.ClipData
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.demo.jetupdates.core.designsystem.R.drawable
import com.demo.jetupdates.core.designsystem.component.AppIconCartToggleButton
import com.demo.jetupdates.core.designsystem.icon.AppIcons
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.model.data.UserShopItem

/**
 * [userShopItem] card used on the following screens: For You, Saved
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemResourceCardForList(
    userShopItem: UserShopItem,
    isAddedToCart: Boolean,
    hasBeenViewed: Boolean,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clickActionLabel = stringResource(R.string.core_ui_card_tap_action)
    val sharingLabel = stringResource(R.string.core_ui_feed_sharing)
    val sharingContent = stringResource(
        R.string.core_ui_feed_sharing_data,
        userShopItem.title,
        userShopItem.description,
    )

    val dragAndDropFlags = if (VERSION.SDK_INT >= VERSION_CODES.N) {
        View.DRAG_FLAG_GLOBAL
    } else {
        0
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        // Use custom label for accessibility services to communicate button's action to user.
        // Pass null for action to only override the label and not the actual action.
        modifier = modifier
            .semantics {
                onClick(label = clickActionLabel, action = null)
            }
            .testTag("itemResourceCard:${userShopItem.id}"),
    ) {
        Column {
            if (!userShopItem.images[0].isNullOrEmpty()) {
                Row {
                    ItemResourceHeaderImageList(userShopItem.images[0])
                }
            }
            Box(
                modifier = Modifier.padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    /*,  horizontalArrangement = Arrangement.spacedBy(
                                        10.dp,
                                        alignment = Alignment.CenterHorizontally,
                                    )*/
                ) {
                    Column(modifier = Modifier.fillMaxWidth((.65f))) {
                        ItemResourceTitleList(
                            userShopItem.description,
                            modifier = Modifier
                                .fillMaxWidth()
                                // .fillMaxWidth((.8f))
                                .dragAndDropSource { _ ->
                                    DragAndDropTransferData(
                                        ClipData.newPlainText(
                                            sharingLabel,
                                            sharingContent,
                                        ),
                                        flags = dragAndDropFlags,
                                    )
                                },
                        )

                        Spacer(modifier = Modifier.height(0.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            /* if (!hasBeenViewed) {
                                 NotificationDot(
                                     color = MaterialTheme.colorScheme.tertiary,
                                     modifier = Modifier.size(8.dp),
                                 )
                                 Spacer(modifier = Modifier.size(6.dp))
                             }*/
                            ItemRateText("40$", userShopItem.type)
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    AddToCartButton(isAddedToCart, onToggleBookmark)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemResourceCardForList2(
    userShopItem: UserShopItem,
    isAddedToCart: Boolean,
    hasBeenViewed: Boolean,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clickActionLabel = stringResource(R.string.core_ui_card_tap_action)
    val sharingLabel = stringResource(R.string.core_ui_feed_sharing)
    val sharingContent = stringResource(
        R.string.core_ui_feed_sharing_data,
        userShopItem.title,
        userShopItem.description,
    )

    /*   val dragAndDropFlags = if (VERSION.SDK_INT >= VERSION_CODES.N) {
           View.DRAG_FLAG_GLOBAL
       } else {
           0
       }*/

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        // Use custom label for accessibility services to communicate button's action to user.
        // Pass null for action to only override the label and not the actual action.
        modifier = modifier
            .semantics {
                onClick(label = clickActionLabel, action = null)
            }
            .testTag("itemResourceCard:${userShopItem.id}"),
    ) {
        Column {
            if (userShopItem.images.isNotEmpty() && !userShopItem.images[0].isNullOrEmpty()) {
                Row {
                    ItemResourceHeaderImageList(userShopItem.images[0])
                }
            }
            Box(
                modifier = Modifier.padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    /*,  horizontalArrangement = Arrangement.spacedBy(
                                        10.dp,
                                        alignment = Alignment.CenterHorizontally,
                                    )*/
                ) {
                    Column(modifier = Modifier.weight((1f))) {
                        ItemResourceTitleList(
                            userShopItem.title,
                            modifier = Modifier.fillMaxWidth(),
                            // .fillMaxWidth((.8f))
                            /* .dragAndDropSource { _ ->
                                 DragAndDropTransferData(
                                     ClipData.newPlainText(
                                         sharingLabel,
                                         sharingContent,
                                     ),
                                     flags = dragAndDropFlags,
                                 )
                             }*/
                        )

                        Spacer(modifier = Modifier.height(0.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            /* if (!hasBeenViewed) {
                                 NotificationDot(
                                     color = MaterialTheme.colorScheme.tertiary,
                                     modifier = Modifier.size(8.dp),
                                 )
                                 Spacer(modifier = Modifier.size(6.dp))
                             }*/
                            ItemRateText("40$", userShopItem.type)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    AddToCartButton(isAddedToCart, onToggleBookmark)
                }
            }
        }
    }
}

@Composable
fun ItemResourceHeaderImageList(
    headerImageUrl: String?,
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val imageLoader = rememberAsyncImagePainter(
        model = headerImageUrl,
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        },
    )
    val isLocalInspection = LocalInspectionMode.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            // Display a progress bar while loading
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop,
            painter = if (isError.not() && !isLocalInspection) {
                imageLoader
            } else {
                painterResource(drawable.core_designsystem_ic_placeholder_default)
            },
            // TODO b/226661685: Investigate using alt text of  image to populate content description
            // decorative image,
            contentDescription = null,
        )
    }
}

@Composable
fun ItemResourceTitleList(
    shopItemTitle: String,
    modifier: Modifier = Modifier,
) {
    Text(
        shopItemTitle,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
fun AddToCartButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppIconCartToggleButton(
        checked = isBookmarked,
        onCheckedChange = { onClick() },
        modifier = modifier.size(32.dp),
        icon = {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = AppIcons.AddToCart,
                contentDescription = stringResource(R.string.core_ui_add_to_cart),
            )
        },
        checkedIcon = {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = AppIcons.RemoveFromCart,
                contentDescription = stringResource(R.string.core_ui_remove_from_cart),
            )
        },
    )
}

@Composable
fun ItemRateText(
    publishDate: String,
    resourceType: String,
) {
    Text(
        publishDate,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Preview("Bookmark Button")
@Composable
private fun BookmarkButtonPreview() {
    AppTheme {
        Surface {
            AddToCartButton(isBookmarked = false, onClick = { })
        }
    }
}

@Preview("Bookmark Button Bookmarked")
@Composable
private fun BookmarkButtonBookmarkedPreview() {
    AppTheme {
        Surface {
            AddToCartButton(isBookmarked = true, onClick = { })
        }
    }
}

@Preview("ItemResourceCardForList2")
@Composable
private fun ExpandedItemResourcePreview(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userItemResources: List<UserShopItem>,
) {
    CompositionLocalProvider(
        LocalInspectionMode provides true,
    ) {
        AppTheme {
            Surface {
                ItemResourceCardForList2(
                    userShopItem = userItemResources[0],
                    isAddedToCart = true,
                    hasBeenViewed = false,
                    onToggleBookmark = {},
                    onClick = {},
                    onCategoryClick = {},
                )
            }
        }
    }
}
