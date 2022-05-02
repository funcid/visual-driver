tasks {
    val modProjects = rootProject.subprojects
        .filter { it.pluginManager.hasPlugin("dev.implario.bundler") }

    val copyBundles = task("copyBundles", Copy::class) {
        modProjects.forEach {
            from(it.buildDir.resolve("libs"))
            include("*-bundle-${it.version}.jar")
        }

        into(buildDir.resolve("bundles"))
    }

    modProjects.forEach { copyBundles.dependsOn(it.tasks.getByName("bundle")) }
    build { dependsOn(copyBundles) }
}
