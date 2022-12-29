@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") apply false
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

allprojects {
    group = "me.func.visual-driver"

    // Версия генерируется автоматически
    val release = project.properties["buildVersion"]

    if (release != null) {
        val build = release.toString().toInt()
        project.version = "" + build / 100 + "." + (build / 10) % 10 + "." + build % 10 + ".RELEASE"
    } else {
        project.version = "3.3.20.TEST"
    }
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
        withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
        withType<Jar>().configureEach { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }
        withType<KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf(
                    "-Xlambdas=indy",
                    "-Xno-param-assertions",
                    "-Xno-receiver-assertions",
                    "-Xno-call-assertions",
                    "-Xbackend-threads=0",
                    "-Xassertions=always-disable",
                    "-Xuse-fast-jar-file-system",
                    "-Xsam-conversions=indy"
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
                    "https://repo.c7x.dev/repository/maven-${
                        if (project.version.toString().contains("SNAPSHOT")) "snapshots" else "releases"
                    }/"
                )
                credentials {
                    username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME")
                            ?: System.getenv("REPO_C7X_USERNAME")
                    password =
                        System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD") ?: System.getenv(
                            "REPO_C7X_PASSWORD"
                        )
                }
            }
        }
    }
}

apiValidation {
    ignoredProjects.addAll(listOf("mod", "graffiti", "graffiti-service"))
}
