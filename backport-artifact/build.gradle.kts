tasks {
    publishAllPublicationsToFuncRepository { dependsOn(":api:publishAllPublicationsToFuncRepository") }
    publishAllPublicationsToMavenLocalRepository { dependsOn(":api:publishAllPublicationsToMavenLocalRepository") }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                group = "me.func"
                artifactId = "animation-api"
                version = project.version.toString()

                distributionManagement {
                    relocation {
                        groupId.set(project.group.toString())
                        artifactId.set("animation-api")
                        version.set(project.version.toString())
                        message.set("api artifact location has been changed")
                    }
                }
            }
        }
    }
}
