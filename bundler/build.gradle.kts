plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

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

dependencies {
    implementation("kamillaova.proguard:proguard-gradle:7.2.2-SNAPSHOT")
}

gradlePlugin {
    plugins {
        create("Anime Mod Bundler") {
            id = "anime.mod-bundler"
            implementationClass = "anime.modbundler.ModBundlerPlugin"
        }
    }
}
