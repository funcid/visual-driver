@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") apply false
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

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))
        withSourcesJar()
    }

    tasks {
        withType<Jar> { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }
        withType<JavaCompile> { options.encoding = "UTF-8" }
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf(
                    "-Xlambdas=indy",
                    "-Xno-param-assertions",
                    "-Xno-receiver-assertions",
                    "-Xno-call-assertions",
                    "-Xbackend-threads=0",
                    "-Xuse-ir",
                    "-Xassertions=always-disable",
                    "-Xuse-fast-jar-file-system",
                    "-Xsam-conversions=indy",
                    "-Xskip-prerelease-check" // IDEA Bug fix
                )
            }
        }
    }

    publishing {
        repositories {
            mavenLocal()
            maven {
                name = "func"
                url = uri(
                    "https://repo.c7x.ru/repository/maven-${
                        if (project.version.toString().contains("SNAPSHOT")) "snapshots" else "releases"
                    }/"
                )
                credentials {
                    username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME")
                    password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD")
                }
            }
        }
    }
}
