@file:Suppress("UnstableApiUsage")

rootProject.name = "animation-api"

pluginManagement {
    repositories {
        maven {
            url = uri("https://repo.c7x.ru/repository/maven-snapshots/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD")
            }
        }
        maven {
            url = uri("https://repo.c7x.ru/repository/maven-releases/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version "1.6.20"
        kotlin("plugin.serialization") version "1.6.20"

        id("dev.implario.bundler") version "9999-SNAPSHOT"
    }
}

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://repo.c7x.ru/repository/maven-snapshots/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD")
            }
        }
        maven {
            url = uri("https://repo.c7x.ru/repository/maven-releases/")
            credentials {
                username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME")
                password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD")
            }
        }
        mavenCentral()
    }

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

arrayOf(
    "api",
    "mod",
    "protocol",
    "experimental",
    "graffiti",
    "graffiti-protocol",
    "graffiti-service",
    "npc",
    "battlepass",
    "dialog",
    "lootbox",
    "protocol-serialization",
    "protocol-mod",
    "backport-artifact",
    "store",
    "health-bar",
    "chat"
).forEach { include(":$it") }
