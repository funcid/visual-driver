plugins {
    `maven-publish`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.20-M1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = project.group
            artifactId = "animation-api-protocol"
            version = project.version.toString()

            from(components["java"])
        }
    }
}
