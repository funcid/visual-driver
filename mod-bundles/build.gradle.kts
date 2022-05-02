tasks {
    val copyBundles = task("copyBundles", Copy::class) {
        rootProject.subprojects
            .filter { it.pluginManager.hasPlugin("dev.implario.bundler") }
            .forEach {
                from(it.buildDir.resolve("libs"))
                include("*-bundle-${it.version}.jar")
            }

        into(buildDir.resolve("bundles"))
    }
    build { dependsOn(copyBundles) }
}
