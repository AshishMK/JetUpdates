/*
 * Copyright 2024 The Android Open Source Project
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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.demo.jetupdates.core.designsystem.component.AppIconToggleButtonFavorite
import com.demo.jetupdates.core.designsystem.icon.AppIcons
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.ui.CategoriesToDrawable.mapDrawables
import com.demo.jetupdates.core.ui.R.string

@Composable
fun TrendingItem(
    id: Int,
    name: String,
    following: Boolean,
    categoryImageUrl: String,
    onClick: () -> Unit,
    onFollowButtonClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    description: String = "",
    isSelected: Boolean = false,
) {
    ListItem(
        leadingContent = {
            TrendingIcon(id, categoryImageUrl, iconModifier.size(48.dp))
        },
        headlineContent = {
            Text(text = name)
        },
        supportingContent = {
            Text(text = description)
        },
        trailingContent = {
            AppIconToggleButtonFavorite(
                checked = following,
                onCheckedChange = onFollowButtonClick,
                icon = {
                    Icon(
                        imageVector = AppIcons.FavoriteBorder,
                        contentDescription = stringResource(
                            id = string.core_ui_trending_card_follow_button_content_desc,
                        ),
                    )
                },
                checkedIcon = {
                    Icon(
                        imageVector = AppIcons.Favorite,
                        contentDescription = stringResource(
                            id = string.core_ui_trending_card_unfollow_button_content_desc,
                        ),
                    )
                },
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                Color.Transparent
            },
        ),
        modifier = modifier
            .semantics(mergeDescendants = true) {
                selected = isSelected
            }
            .clickable(enabled = true, onClick = onClick),
    )
}

@Composable
private fun TrendingIcon(id: Int, categoryImageUrl: String, modifier: Modifier = Modifier) {
// if (categoryImageUrl.isEmpty()) {
    Icon(
        modifier = modifier
            //  .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp),
        painter = painterResource(id = mapDrawables[id]!!),
        // decorative image
        contentDescription = null,
    )
    /*  } else {
          DynamicAsyncImage(
              imageUrl = categoryImageUrl,
              contentDescription = null,
              modifier = modifier,
          )
      }*/
}

@Preview
@Composable
private fun InterestsCardPreview() {
    AppTheme {
        Surface {
            TrendingItem(
                name = "Compose",
                id = 1,
                description = "Description",
                following = false,
                categoryImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun InterestsCardLongNamePreview() {
    AppTheme {
        Surface {
            TrendingItem(
                id = 2,
                name = "This is a very very very very long name",
                description = "Description",
                following = true,
                categoryImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun InterestsCardLongDescriptionPreview() {
    AppTheme {
        Surface {
            TrendingItem(
                id = 1,
                name = "Compose",
                description = "This is a very very very very very very very " +
                    "very very very long description",
                following = false,
                categoryImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun InterestsCardWithEmptyDescriptionPreview() {
    AppTheme {
        Surface {
            TrendingItem(
                id = 2,
                name = "Compose",
                description = "",
                following = true,
                categoryImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
            )
        }
    }
}

@Preview
@Composable
private fun InterestsCardSelectedPreview() {
    AppTheme {
        Surface {
            TrendingItem(
                id = 3,
                name = "Compose",
                description = "",
                following = true,
                categoryImageUrl = "",
                onClick = { },
                onFollowButtonClick = { },
                isSelected = true,
            )
        }
    }
}
