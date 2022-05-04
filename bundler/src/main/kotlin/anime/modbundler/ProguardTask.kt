@file:Suppress("UnstableApiUsage")

package anime.modbundler

import anime.getResource
import org.gradle.api.JavaVersion
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import proguard.gradle.ProGuardTask
import java.nio.file.Files

@CacheableTask
open class ProguardTask : ProGuardTask() {
    @get:[InputFile Classpath]
    val input: RegularFileProperty = project.objects.fileProperty()

    @get:OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty()

    @get:Input
    val mainClass: Property<String> = project.objects.property()

    @TaskAction
    override fun proguard() {
        project.configurations
            .getByName("compileClasspath")
            .resolve()
            .map { it.absolutePath }
            .toMutableList()
            .apply {
                removeAll(
                    project.configurations
                        .getByName("runtimeClasspath")
                        .resolve()
                        .map { it.absolutePath }
                )
            }.forEach(::libraryjars)

        libraryjars(
            if (JavaVersion.current().isJava9Compatible) {
                "${System.getProperty("java.home")}/jmods"
            } else {
                "${System.getProperty("java.home")}/lib/rt.jar"
            }
        )

        injars(input.get())
        outjars(outputFile.get())
        configuration(this::class.getResource("/proguard.pro").apply {
            Files.write(toPath(), readText().replace("MAINCLASS", mainClass.get()).toByteArray())
        })

        super.proguard()
    }
}
