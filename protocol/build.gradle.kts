tasks {
    build {
        doFirst {
            println(" --> :protocol:build")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "animation-api-protocol"

            from(components["java"])
        }
    }
}
