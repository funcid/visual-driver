@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://repo.c7x.ru/repository/maven-public/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD")
            }
        }
    }

    includeBuild("bundler")

    plugins {
        kotlin("jvm") version "1.7.0"
        kotlin("plugin.serialization") version "1.6.21"
        id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.10.1"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://repo.c7x.ru/repository/maven-public/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD")
            }
        }
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
