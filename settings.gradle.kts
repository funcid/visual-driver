@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://repo.c7x.dev/repository/maven-public/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME") ?: System.getenv("REPO_C7X_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD") ?: System.getenv("REPO_C7X_PASSWORD")
            }
        }
    }

    includeBuild("bundler")

    plugins {
        kotlin("jvm") version "1.6.21"
        kotlin("plugin.serialization") version "1.6.21"
        id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.10.1"
        id("com.github.johnrengelman.shadow") version "7.1.2"
        id("org.hidetake.ssh") version "2.10.1"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://repo.c7x.dev/repository/maven-public/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME") ?: System.getenv("REPO_C7X_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD") ?: System.getenv("REPO_C7X_PASSWORD")
            }
        }
    }

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "visual-driver"

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
