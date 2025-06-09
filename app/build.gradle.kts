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
import com.demo.jetupdates.AppBuildType

plugins {
    alias(libs.plugins.jetupdates.android.application)
    alias(libs.plugins.jetupdates.android.application.compose)
    alias(libs.plugins.jetupdates.android.application.flavors)
    alias(libs.plugins.jetupdates.hilt)
    alias(libs.plugins.jetupdates.android.application.jacoco)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.kotlin.serialization)

    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.demo.jetupdates"

    defaultConfig {
        applicationId = "com.demo.jetupdates"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.demo.jetupdates.core.testing.AppTestRunner"

    }

 /*   buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }*/

    buildTypes {
        debug {
            applicationIdSuffix = AppBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            applicationIdSuffix = AppBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.named("debug").get()
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

}

dependencies {

    implementation(projects.core.ui)
    implementation(projects.feature.store)
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.sync.work)
    implementation(projects.core.data)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.kotlinx.serialization.json)





    ksp(libs.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.testManifest)
    debugImplementation(projects.uiTestHiltManifest)

    kspTest(libs.hilt.compiler)
    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.dataTest)
    testDemoImplementation(projects.core.screenshotTesting)
    testDemoImplementation(libs.robolectric)
    testDemoImplementation(libs.roborazzi)
    testImplementation(projects.sync.syncTest)
    testImplementation(libs.kotlin.test)

    testImplementation(projects.core.datastoreTest)

    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(projects.core.dataTest)
    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.kotlin.test)
    androidTestImplementation(projects.core.datastoreTest)
    baselineProfile(projects.benchmarks)
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false

    // Make use of Dex Layout Optimizations via Startup Profiles
    dexLayoutOptimization = true
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}