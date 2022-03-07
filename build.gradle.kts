import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.6.20-M1" apply false
    kotlin("plugin.serialization") version "1.6.20-M1" apply false
}

allprojects {
    group = "me.func.animation-api"
    version = "live-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")

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

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.20-M1")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))
        withSourcesJar()
    }

    tasks {
        withType<KotlinCompile> { kotlinOptions { jvmTarget = "1.8" } }
        withType<JavaCompile> { options.encoding = "UTF-8" }
    }

    publishing {
        repositories {
            mavenLocal()
            maven {
                name = "func"
                url = uri("https://repo.implario.dev/cristalix")
                credentials {
                    username = System.getenv("IMPLARIO_REPO_USER")
                    password = System.getenv("IMPLARIO_REPO_PASSWORD")
                }
            }
        }
    }
}
