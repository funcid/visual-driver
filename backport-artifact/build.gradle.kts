dependencies {
    api(project(":api"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.func"
            artifactId = "animation-api"

            from(components["java"])
        }
    }
}
