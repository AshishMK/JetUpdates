package com.demo.jetupdates.feature.foryou

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.demo.designsystem.component.AppBackground
import com.demo.designsystem.component.AppButton
import com.demo.designsystem.theme.AppTheme
import com.demo.jetupdates.core.model.data.UserShopResource
import com.demo.jetupdates.core.ui.DevicePreviews
import com.demo.jetupdates.core.ui.PreviewParameterData
import com.demo.jetupdates.core.ui.TrackScrollJank
import com.demo.jetupdates.core.ui.UserShopResourcePreviewParameterProvider

@Composable
internal fun StoreScreenRoute(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val userShopResources: List<UserShopResource> =
        remember { PreviewParameterData.newsResources.toMutableStateList() }
    val followableCategories: List<FollowableCategory> = remember {
        PreviewParameterData.topics.distinctBy { it.id }.map {  FollowableCategory(it, false)}.toMutableStateList()
    }
    StoreScreen(userShopResources, followableCategories, {}, { _, _ -> followableCategories[0].isFollowed = true}, {}, modifier)
}

@Composable
internal fun StoreScreen(
    userShopResources: List<UserShopResource>,
    followableCategories: List<FollowableCategory>,
    onItemClick: (String) -> Unit,
    onCategoryCheckedChanged: (String, Boolean) -> Unit,
    saveFollowedTopics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberLazyStaggeredGridState()
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(300.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 24.dp,
            modifier = Modifier
                .testTag("store:feed"),
            state = state
        ) {
            onboarding(
                followableCategories = followableCategories,
                onCategoryCheckedChanged = onCategoryCheckedChanged,
                saveFollowedTopics = saveFollowedTopics,
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
        }
    }
}

/**
 * An extension on [LazyListScope] defining the onboarding portion of the for you screen.
 * Depending on the [onboardingUiState], this might emit no items.
 *
 */
private fun LazyStaggeredGridScope.onboarding(
    followableCategories: List<FollowableCategory>,
    onCategoryCheckedChanged: (String, Boolean) -> Unit,
    saveFollowedTopics: () -> Unit,
    interestsItemModifier: Modifier = Modifier,
) {

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
                followableCategories,
                onCategoryCheckedChanged,
                Modifier.padding(bottom = 8.dp),
            )
            // Done button
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                AppButton(
                    onClick = saveFollowedTopics,
                    enabled = false,
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

@Composable
private fun ItemSelection(
    followableCategories: List<FollowableCategory>,
    onCategoryCheckedChanged: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,

    ) {
    val lazyGridState = rememberLazyGridState()
    val topicSelectionTestTag = "store:topicSelection"

    TrackScrollJank(scrollableState = lazyGridState, stateName = topicSelectionTestTag)

    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        LazyHorizontalGrid(
            state = lazyGridState,
            rows = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                .heightIn(max = max(240.dp, with(LocalDensity.current) { 240.sp.toDp() }))
                .fillMaxWidth()
                .testTag(topicSelectionTestTag),
        ) {
            items(
                items = followableCategories,
                key = { it.category.id }) {
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
    categoryId: String,
    imageUrl: String,
    isSelected: Boolean,
    onClick: (String, Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(312.dp)
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
        selected = isSelected,
        onClick = {
            onClick(categoryId, !isSelected)
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 8.dp),
        ) {
            ItemIcon(
                imageUrl = imageUrl,
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .weight(1f),
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
    Image(
        modifier = modifier
            .padding(10.dp)
            .size(32.dp)
            .clip(CircleShape)                       // clip to the circle shape
            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),

        contentScale = ContentScale.Crop,
        painter = painterResource(com.demo.designsystem.R.drawable.core_designsystem_ic_placeholder_default),
        contentDescription = null,
        colorFilter = null,
    )
    /*  DynamicAsyncImage(
          placeholder = painterResource(R.drawable.feature_store_ic_icon_placeholder),
          imageUrl = imageUrl,
          // decorative
          contentDescription = null,
          modifier = modifier
              .padding(10.dp)
              .size(32.dp),
      )*/
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
                categoryId = "1",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
                isSelected = false,
                onClick = { _, _ -> })

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
                categoryId = "1",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
                isSelected = true,
                onClick = { _, _ -> })

        }
    }
}

@DevicePreviews
@Composable
fun ForYouScreenTopicSelection(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userShopResources: List<UserShopResource>,
) {
    AppTheme {
        StoreScreen(
            userShopResources = userShopResources,
            followableCategories = userShopResources.flatMap { it.followableCategories }
                .distinctBy { it.category.id }.map { FollowableCategory(it.category,it.isFollowed) },
            onItemClick = {},
            onCategoryCheckedChanged = { _, _ -> },
            saveFollowedTopics = {}
        )
    }
}


