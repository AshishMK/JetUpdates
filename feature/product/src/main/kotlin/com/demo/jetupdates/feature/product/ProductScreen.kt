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

@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)

package com.demo.jetupdates.feature.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.demo.jetupdates.core.designsystem.component.AppBackground
import com.demo.jetupdates.core.designsystem.icon.AppIcons
import com.demo.jetupdates.core.designsystem.theme.AppTheme
import com.demo.jetupdates.core.model.data.UserShopItem
import com.demo.jetupdates.core.ui.DevicePreviews
import com.demo.jetupdates.core.ui.LocalNavAnimatedVisibilityScope
import com.demo.jetupdates.core.ui.LocalSharedTransitionScope
import com.demo.jetupdates.core.ui.R
import com.demo.jetupdates.core.ui.SnackSharedElementKey
import com.demo.jetupdates.core.ui.SnackSharedElementType
import com.demo.jetupdates.core.ui.UserShopResourcePreviewParameterProvider
import com.demo.jetupdates.core.ui.nonSpatialExpressiveSpring
import com.demo.jetupdates.core.ui.snackDetailBoundsTransform
import com.demo.jetupdates.feature.product.ProductUiState.Success

@Composable
internal fun ProductRoute(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = hiltViewModel(),
) {
    val productUiState: ProductUiState by viewModel.productUiState.collectAsStateWithLifecycle()
    when (productUiState) {
        is Success -> {
            ProductScreen(
                productUiState = (productUiState as Success).product,
                id = viewModel.route.initialProductId,
                windowAdaptiveInfo = windowAdaptiveInfo,
                onBackClick = onBackClick,
                onCartChanged = viewModel::bookmarkItem,
                modifier = modifier,
            )
        }

        else -> {}
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ProductScreen(
    productUiState: UserShopItem,
    id: Int,
    onCartChanged: (Boolean) -> Unit,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    /*val insets = WindowInsets.safeDrawing
    .only(
        WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal,
    )*/
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No Scope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No Scope found")
    val roundedCornerAnim by animatedVisibilityScope.transition
        .animateDp(label = "rounded corner") { enterExit: EnterExitState ->
            when (enterExit) {
                EnterExitState.PreEnter -> 20.dp
                EnterExitState.Visible -> 0.dp
                EnterExitState.PostExit -> 20.dp
            }
        }
    val state = rememberScrollState()
    val isCompact =
        windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    with(sharedTransitionScope) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(roundedCornerAnim))
                .sharedBounds(
                    rememberSharedContentState(
                        key = SnackSharedElementKey(
                            snackId = id,
                            origin = id.toString(),
                            type = SnackSharedElementType.Bounds,
                        ),
                    ),
                    animatedVisibilityScope,
                    clipInOverlayDuringTransition =
                    OverlayClip(RoundedCornerShape(roundedCornerAnim)),
                    boundsTransform = snackDetailBoundsTransform,
                    exit = fadeOut(nonSpatialExpressiveSpring()),
                    enter = fadeIn(nonSpatialExpressiveSpring()),
                )
                .fillMaxSize(),

        ) {
            FlowRow(
                modifier = Modifier.testTag("Scroll_Product_Details")
                    .fillMaxSize()
                    .verticalScroll(state),
                //  .windowInsetsPadding(insets),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                maxItemsInEachRow = 2,
            ) {
                ContentImages(
                    isCompact,
                    productUiState.images,
                    id = id,
                    Modifier.widthIn(max = 600.dp),
                )
                ContentDescription(Modifier.weight(1f), isCompact, productUiState, id)
                // ContentDescription(Modifier.weight(1.0f))
            }
            val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                ?: throw IllegalArgumentException("No Scope found")
            with(animatedVisibilityScope) {
                IconButton(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 3f)
                        .statusBarsPadding()
                        .animateEnterExit(
                            enter = scaleIn(tween(300, delayMillis = 300)),
                            exit = scaleOut(tween(20)),
                        )
                        .background(
                            color = Color(0xff121212).copy(alpha = .32f),
                            shape = CircleShape,
                        ),
                    /* Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Top,
                        ),
                    ),*/
                    // .padding(top = WindowInsets.statusBars.getTop(LocalDensity.current).dp, bottom = WindowInsets.statusBars.getBottom(LocalDensity.current).dp)
                ) {
                    Icon(
                        imageVector = AppIcons.ArrowBack,
                        contentDescription = stringResource(
                            id = R.string.core_ui_back,
                        ),
                    )
                }
            }

            CartButton(
                state = state,
                isCompact = isCompact,
                userShopItem = productUiState,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onCartChanged = onCartChanged,
            )
        }
    }
}

private val roundShape = RoundedCornerShape(40.dp, 40.dp, 40.dp, 40.dp)
private val roundShapeCompat = RoundedCornerShape(0.dp, 0.dp, 40.dp, 40.dp)

@Composable
internal fun ContentImages(
    isCompact: Boolean,
    images: List<String>,
    id: Int,
    modifier: Modifier = Modifier,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalStateException("No sharedTransitionScope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalStateException("No animatedVisibilityScope found")

    with(sharedTransitionScope) {
        if (isCompact) {
            Box(contentAlignment = Alignment.BottomCenter) {
                CarouselExample_MultiBrowse(
                    images,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(
                            key = SnackSharedElementKey(
                                snackId = id,
                                origin = id.toString(),
                                type = SnackSharedElementType.Image,
                            ),
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                        exit = fadeOut(),
                        enter = fadeIn(),
                        boundsTransform = snackDetailBoundsTransform,
                    ),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp),
                ) {
                    NotificationDot(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(10.dp),
                    )
                    NotificationDot(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(10.dp),
                    )
                }
            }
            /*  Image(
              modifier = modifier
                  .height(400.dp)
                  .shadow(
                      elevation = 2.dp,
                      ambientColor = MaterialTheme.colorScheme.error,
                      spotColor = MaterialTheme.colorScheme.error,
                      shape = roundShapeCompat,
                  ),
             */
            /*.clip(
                            roundShapeCompat),*/
            /*
            contentScale = ContentScale.Crop,
            painter = painterResource(com.demo.jetupdates.core.designsystem.R.drawable.core_designsystem_ic_placeholder_default),
            contentDescription = null,
        )*/
        } else {
            Row(
                modifier = modifier
                    .height(400.dp)
                    .border(3.dp, MaterialTheme.colorScheme.primaryContainer, roundShape)
                    .clip(
                        roundShape,
                    ),
            ) {
                ProductImageLoader(
                    headerImageUrl = images[0],
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(
                                key = SnackSharedElementKey(
                                    snackId = id,
                                    origin = id.toString(),
                                    type = SnackSharedElementType.Image,
                                ),
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            exit = fadeOut(),
                            enter = fadeIn(),
                            boundsTransform = snackDetailBoundsTransform,
                        )
                        .weight(1f)
                        .fillMaxHeight(),
                )
                ProductImageLoader(
                    headerImageUrl = images[1],
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
                /*         Image(
                             modifier = Modifier
                                 .sharedBounds(
                                     rememberSharedContentState(
                                         key = SnackSharedElementKey(
                                             snackId = id,
                                             origin = id.toString(),
                                             type = SnackSharedElementType.Image,
                                         ),
                                     ),
                                     animatedVisibilityScope = animatedVisibilityScope,
                                     exit = fadeOut(),
                                     enter = fadeIn(),
                                     boundsTransform = snackDetailBoundsTransform,
                                 )
                                 .weight(1f)
                                 .fillMaxHeight(),
                             contentScale = ContentScale.Crop,
                             painter = painterResource(com.demo.jetupdates.core.designsystem.R.drawable.core_designsystem_ic_placeholder_default),
                             contentDescription = null,
                         )*/
                /*  Image(
                      modifier = Modifier
                          .weight(1f)
                          .fillMaxHeight(),
                      contentScale = ContentScale.Crop,
                      painter = painterResource(com.demo.jetupdates.core.designsystem.R.drawable.core_designsystem_ali),
                      contentDescription = null,
                  )*/
            }
        }
    }
}

@Composable
fun ProductImageLoader(
    headerImageUrl: String,
    modifier: Modifier = Modifier,
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

    if (isLoading) {
        // Display a progress bar while loading
        /*    CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )*/
    }

    Image(
        modifier = modifier,
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

@Composable
internal fun ContentDescription(
    isCompact: Boolean = true,
    userShopItem: UserShopItem,
    id: Int,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
        ?: throw IllegalArgumentException("No Scope found")
    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
        ?: throw IllegalArgumentException("No Scope found")
    Column(
        modifier = modifier
            .heightIn(min = 400.dp)
            .padding(all = 16.dp),
        verticalArrangement = if (isCompact) Arrangement.Top else Arrangement.Center,

    ) {
        with(sharedTransitionScope) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    4.dp,
                    alignment = Alignment.CenterHorizontally,
                ),
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                ShopItemTitle(
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(
                                key = SnackSharedElementKey(
                                    snackId = id,
                                    origin = id.toString(),
                                    type = SnackSharedElementType.Title,
                                ),
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = snackDetailBoundsTransform,
                        )
                        .weight(1f),
                    newsResourceTitle = userShopItem.title,

                )

                Text(
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(
                                key = SnackSharedElementKey(
                                    snackId = id,
                                    origin = id.toString(),
                                    type = SnackSharedElementType.Tagline,
                                ),
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = snackDetailBoundsTransform,
                        )
                        .padding(top = if (isCompact) (LocalDensity.current.density * 1.7).dp else 0.dp) // (LocalDensity.current.density * 2.2).dp)
                        .wrapContentWidth(),
                    textAlign = TextAlign.End,
                    text = userShopItem.price.toInt().toString() + "$",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
                //  Spacer(modifier = Modifier.weight(1f))
                //   BookmarkButton(false, { })
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        val sharedTransitionScope =
            LocalSharedTransitionScope.current ?: throw IllegalStateException("No scope found")
        with(sharedTransitionScope) {
            Text(
                modifier = Modifier
                    .skipToLookaheadSize()
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = userShopItem.description, // +"\n\n" + shopItems[0].description,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun ShopItemTitle(
    newsResourceTitle: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        textAlign = TextAlign.Start,
        text = newsResourceTitle,
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
fun BoxScope.CartButton(
    state: ScrollState,
    isCompact: Boolean,
    userShopItem: UserShopItem,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onCartChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        val isExpanded = state.lastScrolledForward ||
            state.canScrollBackward || !isCompact
        ExtendedFloatingActionButton(

            text = { Text(text = stringResource(id = if (userShopItem.isSaved) R.string.core_ui_unbookmark else R.string.core_ui_bookmark)) },
            icon = {
                if (userShopItem.isSaved) {
                    Icon(
                        AppIcons.RemoveFromCart,
                        stringResource(id = R.string.core_ui_unbookmark),
                    )
                } else {
                    Icon(AppIcons.AddToCart, stringResource(id = R.string.core_ui_bookmark))
                }
            },
            onClick = { onCartChanged(!userShopItem.isSaved) },
            modifier = Modifier.testTag("Product_FAB").semantics {
                contentDescription = if (isExpanded) "Product_FAB_Expanded" else "Product_FAB_Collapsed"
            }
                .sharedBounds(
                    rememberSharedContentState(
                        key = SnackSharedElementKey(
                            snackId = userShopItem.id,
                            origin = userShopItem.id.toString(),
                            type = SnackSharedElementType.FAB,
                        ),
                    ),
                    animatedVisibilityScope = animatedVisibilityScope,
                    exit = fadeOut(),
                    enter = fadeIn(),
                    boundsTransform = snackDetailBoundsTransform,
                )
                .padding(16.dp)
                .systemBarsPadding()
                .align(if (isCompact) Alignment.BottomEnd else Alignment.BottomCenter),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            expanded = isExpanded,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarouselExample_MultiBrowse(images: List<String>, modifier: Modifier) {
    val items = remember {
        listOf(
            com.demo.jetupdates.core.designsystem.R.drawable.core_designsystem_ali,
            com.demo.jetupdates.core.designsystem.R.drawable.core_designsystem_someone_else,
        )
    }

    HorizontalMultiBrowseCarousel(

        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .height(400.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                ambientColor = MaterialTheme.colorScheme.error,
                spotColor = MaterialTheme.colorScheme.error,
                shape = roundShapeCompat,
            ),
        preferredItemWidth = 400.dp,
        itemSpacing = 0.dp,
        contentPadding = PaddingValues(horizontal = 0.dp),
    ) { i ->
        if (i == 0) {
            ProductImageLoader(
                headerImageUrl = images[0],
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            )
            /*     Image(
                     modifier = modifier
                         .fillMaxHeight(),
                     //       .maskClip(MaterialTheme.shapes.extraLarge),
                     painter = painterResource(id = item),
                     contentDescription = null,
                     contentScale = ContentScale.Crop,
                 )*/
        } else {
            ProductImageLoader(
                headerImageUrl = images[1],
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            )
            /*   Image(
                   modifier = Modifier
                       .fillMaxHeight(),
                   //       .maskClip(MaterialTheme.shapes.extraLarge),
                   painter = painterResource(id = item),
                   contentDescription = null,
                   contentScale = ContentScale.Crop,
               )*/
        }
    }
}

@Composable
fun NotificationDot(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.core_ui_pager_image_dot_content_description)
    Canvas(
        modifier = modifier
            .semantics { contentDescription = description },
        onDraw = {
            /*   drawCircle(
                   color,
                   radius = size.minDimension / 2,
               )*/
            drawCircle(
                Color.White,
                radius = size.minDimension / 2,
                style = Stroke(
                    width = 4.dp.value,
                ),
            )

            drawCircle(
                color = color,
                radius = (size.minDimension - 4.dp.value) / 2,

            )
        },
    )
}

@Preview
@Composable
fun ContentImagesLandscapePreview(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userShopItems: List<UserShopItem>,
) {
    AppTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalNavAnimatedVisibilityScope provides this,
                ) {
                    ContentImages(
                        isCompact = false,
                        images = userShopItems[0].images,
                        id = userShopItems[0].id,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ContentImagesCarouselPreview(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userShopItems: List<UserShopItem>,
) {
    AppTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalNavAnimatedVisibilityScope provides this,
                ) {
                    ContentImages(
                        isCompact = true,
                        images = userShopItems[0].images,
                        id = userShopItems[0].id,
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
fun ProductScreenLoaded(
    @PreviewParameter(UserShopResourcePreviewParameterProvider::class)
    userShopItems: List<UserShopItem>,
) {
    AppTheme {
        AppBackground {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        LocalNavAnimatedVisibilityScope provides this,
                    ) {
                        ProductScreen(
                            productUiState = userShopItems[0],
                            id = userShopItems[0].id,
                            onCartChanged = {},
                            windowAdaptiveInfo = currentWindowAdaptiveInfo(),
                            onBackClick = {},
                        )
                    }
                }
            }
        }
    }
}
