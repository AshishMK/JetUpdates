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
import com.android.build.api.variant.BuildConfigField
import java.io.StringReader
import java.util.Properties

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

plugins {
    alias(libs.plugins.jetupdates.android.library)
    alias(libs.plugins.jetupdates.android.library.jacoco)
    alias(libs.plugins.jetupdates.hilt)
    id("kotlinx-serialization")
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.demo.jetupdates.core.network"
    testOptions.unitTests.isIncludeAndroidResources = true
}
/*
secrets {
    propertiesFileName = "mykeys.properties"
    defaultPropertiesFileName = "secrets.defaults.properties"
}*/

dependencies {
    api(libs.kotlinx.datetime)
    api(projects.core.common)
    api(projects.core.model)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.svg)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)

    testImplementation(libs.kotlinx.coroutines.test)
}


val propertyTextProvider = providers.fileContents(
    isolated.rootProject.projectDirectory.file("mykeys.properties")
).asText

val backendUrl =  propertyTextProvider.map { text ->
    val properties = Properties()
    properties.load(StringReader(text))
    properties["BACKEND_URL"]
    // Move to returning `properties["BACKEND_URL"] as String?` after upgrading to Gradle 9.0.0
}.orElse("http://example2.com")

val apiKey =  propertyTextProvider.map { text ->
    val properties = Properties()
    properties.load(StringReader(text))
    if (properties.containsKey("API_KEY"))
        (properties["API_KEY"] as String)
    else "test"
    // Move to returning `properties["BACKEND_URL"] as String?` after upgrading to Gradle 9.0.0
}.orElse("test3")

androidComponents {
    onVariants {
        it.buildConfigFields!!.put("BACKEND_URL", backendUrl.map { value ->
            BuildConfigField(type = "String", value = """"$value"""", comment = null)
        })

        it.buildConfigFields!!.put("API_KEY", apiKey.map { value ->
            BuildConfigField(type = "String", value = """"$value"""", comment = null)
        })
    }
}
