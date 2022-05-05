publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "animation-api-protocol"
            version = project.version.toString()

            from(components["java"])
        }
    }
}
