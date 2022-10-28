dependencies {
    compileOnly("com.google.code.gson:gson:2.9.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "animation-api-protocol"

            from(components["java"])
        }
    }
}
