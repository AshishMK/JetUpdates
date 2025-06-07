package com.demo.jetupdates

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import com.demo.jetupdates.FlavorDimension.contentType

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

// The content for the app can either come from local static data which is useful for demo
// purposes, or from a production backend server which supplies up-to-date, real content.
// These two product flavors reflect this behaviour.
@Suppress("EnumEntryName")
enum class AppFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    demo(contentType, applicationIdSuffix = ".demo"),
    prod(contentType),
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: AppFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            AppFlavor.values().forEach { appFlavor ->
                register(appFlavor.name) {
                    dimension = appFlavor.dimension.name
                    flavorConfigurationBlock(this, appFlavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (appFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = appFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}
