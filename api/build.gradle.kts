dependencies {
    compileOnly("cristalix:bukkit-core:21.01.30")
    compileOnly("cristalix:dark-paper:21.02.03")

    implementation("dev.xdark:feder:1.0")
    implementation("com.github.ben-manes.caffeine:caffeine:2.9.3")
    implementation("org.apache.logging.log4j:log4j-jul:2.17.2")

    api(project(":protocol"))
    api(project(":graffiti-protocol"))
    api(project(":protocol-serialization"))
}

val bundle: Provider<Directory> = project(":mod").layout.buildDirectory.dir("bundle")

tasks {
    jar {
        inputs.dir(bundle)
        dependsOn(":mod:proguardJar")
    }
}

sourceSets {
    main {
        resources.srcDir(bundle)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "animation-api"

            from(components["java"])
        }
    }
}
