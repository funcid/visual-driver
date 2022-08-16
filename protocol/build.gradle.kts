publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "animation-api-protocol"

            from(components["java"])
        }
    }
}
