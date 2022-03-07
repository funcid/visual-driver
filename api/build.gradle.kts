dependencies {
    compileOnlyApi("cristalix:dark-paper:21.02.03")

    implementation("dev.xdark:feder:live-SNAPSHOT")
    api("cristalix:bukkit-core:21.01.30")
    api(project(":protocol"))
    api(project(":graffiti-protocol"))
    api(project(":protocol-serialization"))
}

// Ого, костыли! Я хз как сделать по нормальному)
tasks {
    publishAllPublicationsToFuncRepository {
        dependsOn(":protocol:publishAllPublicationsToFuncRepository")
        dependsOn(":graffiti-protocol:publishAllPublicationsToFuncRepository")
        dependsOn(":protocol-serialization:publishAllPublicationsToFuncRepository")
    }
    publishAllPublicationsToMavenLocalRepository {
        dependsOn(":protocol:publishAllPublicationsToMavenLocalRepository")
        dependsOn(":graffiti-protocol:publishAllPublicationsToMavenLocalRepository")
        dependsOn(":protocol-serialization:publishAllPublicationsToMavenLocalRepository")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = project.group
            artifactId = "animation-api"
            version = project.version.toString()

            from(components["java"])
        }
    }
}
