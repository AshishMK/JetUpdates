
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
include(":feature:store")
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
include(":sync:work")
include(":core:datastore-test")
include(":sync:sync-test")
include(":benchmarks")
include(":feature:cart")
include(":feature:trending")
include(":feature:category")
include(":feature:search")
include(":feature:settings")
include(":feature:chat")
include(":feature:product")
check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17)) {
    """
    JU App requires JDK 17+ but it is currently using JDK ${JavaVersion.current()}.
    Java Home: [${System.getProperty("java.home")}]
    https://developer.android.com/build/jdks#jdk-config-in-studio
    """.trimIndent()
}

