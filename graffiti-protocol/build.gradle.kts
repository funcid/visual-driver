plugins {
    `maven-publish`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.20-M1")
    implementation("cristalix:bukkit-core:21.01.30")
    api(project(":protocol"))
    api(project(":protocol-serialization"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = project.group
            artifactId = "animation-api-graffiti-protocol"
            version = project.version.toString()

            from(components["java"])
        }
    }
}
