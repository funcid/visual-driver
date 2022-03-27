@file:Suppress("UnstableApiUsage")

rootProject.name = "animation-api"

pluginManagement {
    repositories {
        maven("https://repo.implario.dev/public")
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version "1.6.20-M1"
        kotlin("plugin.serialization") version "1.6.20-M1"

        id("dev.implario.bundler") version "3.0.2"
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://repo.implario.dev/public/")
        maven {
            url = uri("https://repo.implario.dev/cristalix/")
            credentials {
                username = System.getenv("IMPLARIO_REPO_USER")
                password = System.getenv("IMPLARIO_REPO_PASSWORD")
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
