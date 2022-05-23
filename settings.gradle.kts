@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        maven {
            url = uri("https://repo.c7x.ru/repository/maven-public/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    includeBuild("bundler")

    plugins {
        kotlin("jvm") version "1.6.21"
        kotlin("plugin.serialization") version "1.6.21"
    }
}

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://repo.c7x.ru/repository/maven-public/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD")
            }
        }
        mavenCentral()
    }

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "animation-api"

arrayOf(
    "api",
    "plugin",
    "mod",
    "protocol",
    "graffiti",
    "graffiti-protocol",
    "graffiti-service",
    "protocol-serialization",
    "protocol-mod",
    "backport-artifact"
).forEach { include(":$it") }
