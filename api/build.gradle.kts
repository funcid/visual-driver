import java.io.FileOutputStream
import java.util.Properties

dependencies {
    compileOnly("cristalix:bukkit-core:21.01.30")
    compileOnly("cristalix:dark-paper:21.02.03")

    implementation("me.func:atlas-api:1.0.10")
    implementation("dev.xdark:feder:1.0")
    implementation("com.github.ben-manes.caffeine:caffeine:2.9.3")
    implementation("org.apache.logging.log4j:log4j-jul:2.17.2")

    api(project(":protocol"))
    api(project(":graffiti-protocol"))
    api(project(":protocol-serialization"))
}

tasks {
    val generateVersionProperties by registering {
        val propertiesFile = file("$buildDir/generated-version/version.properties")
        propertiesFile.parentFile.mkdirs()
        val properties = Properties()
        properties.setProperty("version", "${project.version}")
        val out = FileOutputStream(propertiesFile)
        properties.store(out, null)
        outputs.file(propertiesFile)
    }
    jar {
        from(generateVersionProperties)
        from(project(":mod").tasks.named("proguardJar").get().outputs)
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
