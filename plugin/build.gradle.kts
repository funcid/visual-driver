plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    compileOnly("cristalix:bukkit-core:21.01.30")
    compileOnly("cristalix:dark-paper:21.02.03")

    implementation("me.func:animation-api:0.0.0-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "animation-plugin"
            version = project.version.toString()

            from(components["java"])
        }
    }
}