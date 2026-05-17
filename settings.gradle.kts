
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

}


dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}
rootProject.name = "JetUpdates"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":lint")
include(":core:designsystem")
include(":core:screenshot-testing")
include(":ui-test-hilt-manifest")
include(":core:testing")
include(":core:ui")

include(":core:model")
include(":core:data")
include(":core:data-test")
include(":core:database")
include(":core:datastore")
include(":core:datastore-proto")
include(":core:common")
include(":core:network")
include(":core:domain")
include(":core:notifications")
include(":core:navigation")
include(":sync:work")
include(":core:datastore-test")
include(":sync:sync-test")
include(":benchmarks")


include(":feature:store")
include(":feature:store:api")
include(":feature:store:impl")
include(":feature:cart:api")
include(":feature:cart:impl")
include(":feature:trending:api")
include(":feature:trending:impl")
include(":feature:category:api")
include(":feature:category:impl")
include(":feature:search:api")
include(":feature:search:impl")
include(":feature:settings:impl")
include(":feature:chat:api")
include(":feature:chat:impl")
include(":feature:product:api")
include(":feature:product:impl")
check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17)) {
    """
    JU App requires JDK 17+ but it is currently using JDK ${JavaVersion.current()}.
    Java Home: [${System.getProperty("java.home")}]
    https://developer.android.com/build/jdks#jdk-config-in-studio
    """.trimIndent()
}

