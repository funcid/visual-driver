package anime.modbundler

import anime.GradlePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

@GradlePlugin
class ModBundlerPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        if (!plugins.hasPlugin("java")) apply(plugin = "java")

        val ext = ModBundlerExtension().also { extensions.add("mod", it) }

        val generateModPropertiesTask =
            tasks.register<GenerateModPropertiesTask>("generateModProperties") {
                outputDirectory.set(layout.buildDirectory.dir("generated/modbundler"))
                desc.set(ext)
            }

        extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
            resources.srcDir(generateModPropertiesTask)
        }

        tasks.register<ProguardTask>("proguardJar") {
            input.set(tasks.getByName<Jar>("jar").archiveFile)
            outputFile.set(project.layout.buildDirectory.file("libs/${project.name}-bundle.jar"))
            mainClass.set(ext.main)
        }.let { tasks.getByName("build").dependsOn(it) }

        tasks.getByName<Jar>("jar") {
            doFirst {
                from(configurations.getByName("runtimeClasspath")
                    .map { if (it.isDirectory) it else zipTree(it) })
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            include("**/*.class", "*.class", "mod.properties")
        }
    }
}
