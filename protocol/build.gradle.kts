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
