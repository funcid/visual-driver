plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.2")
    api(project(":protocol"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "animation-api-protocol-serialization"
            version = project.version.toString()

            from(components["java"])
        }
    }
}
