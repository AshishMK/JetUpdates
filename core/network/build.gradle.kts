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
    alias(libs.plugins.apollo) // Apply Apollo plugin
    alias(libs.plugins.kotlin.serialization)
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.demo.jetupdates.core.network"
    testOptions.unitTests.isIncludeAndroidResources = true
}

// Configure package name for generated Apollo Kotlin classes
apollo {
    service("service") {
        packageName.set("com.demo.jetupdates.core.network")
        introspection {
            endpointUrl.set("https://ap-south-1.cdn.hygraph.com/content/cmsbg4y1q01ek07uuncinbazq/master")
            schemaFile.set(file("src/main/graphql/com/demo/jetupdates/core/network/schema.graphqls"))
            headers.put("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImdjbXMtbWFpbi1wcm9kdWN0aW9uIn0.eyJ2ZXJzaW9uIjozLCJpYXQiOjE3ODgxNzQ1NjIsImF1ZCI6WyJodHRwczovL2FwaS1hcC1zb3V0aC0xLmh5Z3JhcGguY29tL3YyL2Ntc2JnNHkxcTAxZWswN3V1bmNpbmJhenEvbWFzdGVyIiwibWFuYWdlbWVudC1uZXh0LmdyYXBoY21zLmNvbSJdLCJpc3MiOiJodHRwczovL21hbmFnZW1lbnQtYXAtc291dGgtMS5oeWdyYXBoLmNvbS8iLCJzdWIiOiI2NTkxNDg2OS1kMDE4LTQ4OTQtODUxZS1kZTg5MTUwMGViNjciLCJqdGkiOiJjbXRoNHlyNmMxNm84MDdvNTQwN2dkbGx5In0.rp1oQnTkz6tn37WcCBCQTH5eGGUDF4s8jqLsYzmvU2Feaf_fipHjdYjD4vxte5OKRAS5md0ZerwwSCj3UWjJSB1QQrYxn9CNXxphDo2NU53N9qpET7xq74EbXALbL2OY31_VG3suFV0S-ORy4x8F0NDx-YEdpBbJgjOzMBxDOnxiePkqbDB4TH3OCH4VWiIDaHiTnWRJNW7XCrj3g8yt8mm5YIkIsOA2i1f11ondFv4UuPmn_e_JeNh9rsqW8uu1XFgPRtYDnAcosW4WLHWQ80MnxKor1vD8G3z36ofD_0-5N-c3HS5I1aphK1JuAoccHZPbeCb827sWs1pNWgf_rRONKQa76QEoyeA-2w7d79c4sSq1-VF4akxeIVbDF6yuT3hgMD5xwX6pVBsXhVUUd5l-_OJreKyh6Z66M5U9WfsYgCnNZdYgES2kmkU8hCShSxHXpJSb9Kuxi4axCizwfs1wkQuVdr7YhJj1e6dUAnqAkdylidvO8g7R38nIhS5j6Rmccj5osxZ5Wg3L-JQS44EoCkFqITr8J0uj_8c2nBdA5hYqeHVHxkaTkkckOI-iOpRGZ0Vrws9wbBGgO3hwwSrI1UiIDr4J7hor6jYKdphS9EAG2HGMqOatsPWFOLjv2yzAueNXo1XX7YEU5YYRerN8WYLwYK4JxpfkkHfYe94")
        }
    }
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
    // Apollo Runtime
    implementation(libs.apollo.runtime)

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
