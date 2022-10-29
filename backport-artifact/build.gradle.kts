dependencies {
    api(project(":api"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.func"
            artifactId = "visual-driver"

            from(components["java"])
        }
    }
}
