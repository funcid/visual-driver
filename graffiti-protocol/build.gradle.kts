dependencies {
    compileOnly("cristalix:bukkit-core:21.01.30")
    api(project(":protocol"))
    api(project(":protocol-serialization"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "visual-driver-graffiti-protocol"

            from(components["java"])
        }
    }
}
