dependencies {
    api(project(":api"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.func"
            artifactId = "animation-api"
            version = project.version.toString()

            from(components["java"])
        }
    }
}
