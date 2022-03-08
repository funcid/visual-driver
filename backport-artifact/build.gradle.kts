tasks {
    publishAllPublicationsToFuncRepository { dependsOn(":api:publishAllPublicationsToFuncRepository") }
    publishAllPublicationsToMavenLocalRepository { dependsOn(":api:publishAllPublicationsToMavenLocalRepository") }
}

dependencies {
    api(project(":api"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = "me.func"
            artifactId = "animation-api"
            version = project.version.toString()

            from(components["java"])
        }
    }
}
