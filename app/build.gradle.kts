import com.demo.jetupdates.NiaBuildType

plugins {
    alias(libs.plugins.jetupdates.android.application)
    alias(libs.plugins.jetupdates.android.application.compose)
}

android {
    namespace = "com.demo.jetupdates"

    defaultConfig {
        applicationId = "com.demo.jetupdates"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.demo.jetupdates.core.testing.NiaTestRunner"

    }

 /*   buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }*/

    buildTypes {
        debug {
            applicationIdSuffix = NiaBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            applicationIdSuffix = NiaBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.named("debug").get()
            // Ensure Baseline Profile is fresh for release builds.
          //  baselineProfile.automaticGenerationDuringBuild = true
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)


    implementation(libs.androidx.core.splashscreen)

}