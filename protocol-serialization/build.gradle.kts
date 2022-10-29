plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.3")
    api(project(":protocol"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "visual-driver-protocol-serialization"

            from(components["java"])
        }
    }
}
