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

package com.demo.jetupdates.core.ui

import com.demo.jetupdates.core.model.data.Message
import com.demo.jetupdates.core.designsystem.R as DesignR

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [Message] for Composable previews.
 */

object CategoriesToDrawable {
    public var mapDrawables = HashMap<Int, Int>().apply {
        put(1, DesignR.drawable.core_designsystem_fashion)
        put(2, DesignR.drawable.core_designsystem_electronics)
        put(3, DesignR.drawable.core_designsystem_home)
        put(4, DesignR.drawable.core_designsystem_beauty)
        put(5, DesignR.drawable.core_designsystem_sports)
        put(6, DesignR.drawable.core_designsystem_toys)
        put(7, DesignR.drawable.core_designsystem_health)
        put(8, DesignR.drawable.core_designsystem_automotive)

        put(9, DesignR.drawable.core_designsystem_books)
        put(10, DesignR.drawable.core_designsystem_grocery)

        put(11, DesignR.drawable.core_designsystem_watches)
        put(12, DesignR.drawable.core_designsystem_pets)
        put(13, DesignR.drawable.core_designsystem_art)
        put(14, DesignR.drawable.core_designsystem_diy)
        put(15, DesignR.drawable.core_designsystem_music)
        put(16, DesignR.drawable.core_designsystem_travel)

        put(17, DesignR.drawable.core_designsystem_office)
        put(18, DesignR.drawable.core_designsystem_media)
        put(19, DesignR.drawable.core_designsystem_eco)
    }
}
