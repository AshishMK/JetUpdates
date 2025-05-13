plugins {
    alias(libs.plugins.jetupdates.android.library)
    alias(libs.plugins.jetupdates.android.library.compose)
    alias(libs.plugins.jetupdates.android.library.jacoco)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.demo.designsystem"


}

dependencies {
    lintPublish(projects.lint)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.adaptive)
    api(libs.androidx.compose.material3.navigationSuite)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.util)

    implementation(libs.coil.kt.compose)
    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.androidx.compose.ui.testManifest)

    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.robolectric)
    testImplementation(projects.core.screenshotTesting)
}