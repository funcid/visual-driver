plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.c7x.ru/repository/maven-public/")
        credentials {
            username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME") ?: System.getenv("REPO_C7X_USERNAME")
            password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD") ?: System.getenv("REPO_C7X_PASSWORD")
        }
    }
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
