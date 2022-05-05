dependencies {
    compileOnly("cristalix:bukkit-core:21.01.30")
    api(project(":protocol"))
    api(project(":protocol-serialization"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "animation-api-graffiti-protocol"
            version = project.version.toString()

            from(components["java"])
        }
    }
}
