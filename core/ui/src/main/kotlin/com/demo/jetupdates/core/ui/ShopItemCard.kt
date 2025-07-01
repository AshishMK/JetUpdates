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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.demo.jetupdates.core.designsystem.component.AppCategoryTag
import com.demo.jetupdates.core.designsystem.component.AppIconToggleButton
import com.demo.jetupdates.core.designsystem.icon.AppIcons
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.model.data.FollowableCategory2
import com.demo.jetupdates.core.model.data.UserShopItem
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * [ShopItem] card used on the following screens: For You, Saved
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShopItemCardExpanded(
    userShopItem: UserShopItem,
    isBookmarked: Boolean,
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

  /*  val dragAndDropFlags = if (VERSION.SDK_INT >= VERSION_CODES.N) {
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
            .testTag(":${userShopItem.id}"),
    ) {
        Column {
            if (!userShopItem.images.isNullOrEmpty()) {
                Row {
                    ShopItemHeaderImage(userShopItem.images[0])
                }
            }
            Box(
                modifier = Modifier.padding(16.dp),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        ShopItemTitle(
                            userShopItem.title,
                            modifier = Modifier
                                .fillMaxWidth((.8f)),

                        )
                        Spacer(modifier = Modifier.weight(1f))
                        BookmarkButton(isBookmarked, onToggleBookmark)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!hasBeenViewed) {
                            NotificationDot(
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(8.dp),
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                        }
                        ShopItemMetaData(userShopItem.publishDate, userShopItem.type)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    ShopItemShortDescription(userShopItem.description)
                    Spacer(modifier = Modifier.height(12.dp))
                    NewsResourceTopics(
                        categories = userShopItem.followableCategories,
                        onCategoryClick = onCategoryClick,
                    )
                }
            }
        }
    }
}

@Composable
fun ShopItemHeaderImage(
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
            .height(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            // Display a progress bar while loading
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(80.dp),
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
                painterResource(com.demo.jetupdates.core.designsystem.R.drawable.core_designsystem_ic_placeholder_default)
            },
            // TODO b/226661685: Investigate using alt text of  image to populate content description
            // decorative image,
            contentDescription = null,
        )
    }
}

@Composable
fun ShopItemTitle(
    newsResourceTitle: String,
    modifier: Modifier = Modifier,
) {
    Text(newsResourceTitle, style = MaterialTheme.typography.headlineSmall, modifier = modifier)
}

@Composable
fun BookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppIconToggleButton(
        checked = isBookmarked,
        onCheckedChange = { onClick() },
        modifier = modifier,
        icon = {
            Icon(
                imageVector = AppIcons.BookmarkBorder,
                contentDescription = stringResource(R.string.core_ui_bookmark),
            )
        },
        checkedIcon = {
            Icon(
                imageVector = AppIcons.Bookmark,
                contentDescription = stringResource(R.string.core_ui_unbookmark),
            )
        },
    )
}

@Composable
fun NotificationDot(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.core_ui_unread_resource_dot_content_description)
    Canvas(
        modifier = modifier
            .semantics { contentDescription = description },
        onDraw = {
            drawCircle(
                color,
                radius = size.minDimension / 2,
            )
        },
    )
}

@Composable
fun dateFormatted(publishDate: Instant): String = DateTimeFormatter
    .ofLocalizedDate(FormatStyle.MEDIUM)
    .withLocale(Locale.getDefault())
    .withZone(LocalTimeZone.current.toJavaZoneId())
    .format(publishDate.toJavaInstant())

@Composable
fun ShopItemMetaData(
    publishDate: Instant,
    resourceType: String,
) {
    val formattedDate = dateFormatted(publishDate)
    Text(
        if (resourceType.isNotBlank()) {
            stringResource(R.string.core_ui_card_meta_data_text, formattedDate, resourceType)
        } else {
            formattedDate
        },
        style = MaterialTheme.typography.labelSmall,
    )
}

@Composable
fun ShopItemShortDescription(
    shopItemShortDescription: String,
) {
    Text(shopItemShortDescription, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun NewsResourceTopics(
    categories: List<FollowableCategory2>,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        // causes narrow chips
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        for (followableCategory in categories) {
            AppCategoryTag(
                followed = followableCategory.isFollowed,
                onClick = { onCategoryClick(followableCategory.category.id) },
                text = {
                    val contentDescription = if (followableCategory.isFollowed) {
                        stringResource(
                            R.string.core_ui_category_chip_content_description_when_followed,
                            followableCategory.category.name,
                        )
                    } else {
                        stringResource(
                            R.string.core_ui_category_chip_content_description_when_not_followed,
                            followableCategory.category.name,
                        )
                    }
                    Text(
                        text = followableCategory.category.name.uppercase(Locale.getDefault()),
                        modifier = Modifier
                            .semantics {
                                this.contentDescription = contentDescription
                            }
                            .testTag("categoryTag:${followableCategory.category.id}"),
                    )
                },
            )
        }
    }
}

@Preview("Bookmark Button")
@Composable
private fun BookmarkButtonPreview() {
    AppTheme {
        Surface {
            BookmarkButton(isBookmarked = false, onClick = { })
        }
    }
}

@Preview("Bookmark Button Bookmarked")
@Composable
private fun BookmarkButtonBookmarkedPreview() {
    AppTheme {
        Surface {
            BookmarkButton(isBookmarked = true, onClick = { })
        }
    }
}

@Preview("ShopItemCardExpanded")
@Composable
private fun ExpandedNewsResourcePreview(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userShopItems: List<UserShopItem>,
) {
    CompositionLocalProvider(
        LocalInspectionMode provides true,
    ) {
        AppTheme {
            Surface {
                ShopItemCardExpanded(
                    userShopItem = userShopItems[0],
                    isBookmarked = true,
                    hasBeenViewed = false,
                    onToggleBookmark = {},
                    onClick = {},
                    onCategoryClick = {},
                )
            }
        }
    }
}
