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

package com.demo.jetupdates.core.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.InboxStyle
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.demo.jetupdates.core.model.data.ShopItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_NUM_NOTIFICATIONS = 5
private const val TARGET_ACTIVITY_NAME = "com.demo.jetupdates.MainActivity"
private const val SHOP_NOTIFICATION_REQUEST_CODE = 0
private const val SHOP_NOTIFICATION_SUMMARY_ID = 1
private const val SHOP_NOTIFICATION_CHANNEL_ID = ""
private const val SHOP_NOTIFICATION_GROUP = "ITEM_NOTIFICATIONS"
private const val DEEP_LINK_SCHEME_AND_HOST = "https://www.nowinandroid.apps.samples.google.com"
private const val DEEP_LINK_FOR_YOU_PATH = "store"
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_FOR_YOU_PATH"
const val DEEP_LINK_SHOP_ITEM_ID_KEY = "linkedStoreItemId"
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_BASE_PATH/{$DEEP_LINK_SHOP_ITEM_ID_KEY}"

/**
 * Implementation of [Notifier] that displays notifications in the system tray.
 */
@Singleton
internal class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {

    override fun postShopNotifications(
        shopItems: List<ShopItem>,
    ) = with(context) {
        if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            return
        }

        val truncatedShopItems = shopItems.take(MAX_NUM_NOTIFICATIONS)

        val shopItemNotifications = truncatedShopItems.map { shopItem ->
            createShopItemNotification {
                setSmallIcon(R.drawable.core_notifications_ic_nia_notification)
                    .setContentTitle(shopItem.title)
                    .setContentText(shopItem.description)
                    .setContentIntent(shopItemPendingIntent(shopItem))
                    .setGroup(SHOP_NOTIFICATION_GROUP)
                    .setAutoCancel(true)
            }
        }
        val summaryNotification = createShopItemNotification {
            val title = getString(
                R.string.core_notifications_shop_item_notification_group_summary,
                truncatedShopItems.size,
            )
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(R.drawable.core_notifications_ic_nia_notification)
                // Build summary info into InboxStyle template.
                .setStyle(shopItemNotificationStyle(truncatedShopItems, title))
                .setGroup(SHOP_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        shopItemNotifications.forEachIndexed { index, notification ->
            notificationManager.notify(
                truncatedShopItems[index].id.hashCode(),
                notification,
            )
        }
        notificationManager.notify(SHOP_NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    /**
     * Creates an inbox style summary notification for shop item updates
     */
    private fun shopItemNotificationStyle(
        shopItems: List<ShopItem>,
        title: String,
    ): InboxStyle = shopItems
        .fold(InboxStyle()) { inboxStyle, shopItem -> inboxStyle.addLine(shopItem.title) }
        .setBigContentTitle(title)
        .setSummaryText(title)
}

/**
 * Creates a notification for configured for shop item updates
 */
private fun Context.createShopItemNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        SHOP_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures that a notification channel is present if applicable
 */
private fun Context.ensureNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        SHOP_NOTIFICATION_CHANNEL_ID,
        getString(R.string.core_notifications_shop_item_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.core_notifications_shop_item_notification_channel_description)
    }
    // Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.shopItemPendingIntent(
    shopItem: ShopItem,
): PendingIntent? = PendingIntent.getActivity(
    this,
    SHOP_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = shopItem.shopItemDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

private fun ShopItem.shopItemDeepLinkUri() = "$DEEP_LINK_BASE_PATH/$id".toUri()
