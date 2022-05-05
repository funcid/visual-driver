dependencies {
    compileOnly("cristalix:bukkit-core:21.01.30")
    compileOnly("cristalix:dark-paper:21.02.03")

    implementation("dev.xdark:feder:1.0")

    api(project(":protocol"))
    api(project(":graffiti-protocol"))
    api(project(":protocol-serialization"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "animation-api"
            version = project.version.toString()

            from(components["java"])
        }
    }
}
