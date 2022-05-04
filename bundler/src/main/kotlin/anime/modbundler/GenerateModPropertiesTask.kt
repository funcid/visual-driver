@file:Suppress("UnstableApiUsage")

package anime.modbundler

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property

@CacheableTask
open class GenerateModPropertiesTask : DefaultTask() {
    @get:Input
    val desc: Property<ModBundlerExtension> = project.objects.property()

    @get:OutputDirectory
    val outputDirectory: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun run() {
        outputDirectory.file("mod.properties").get().asFile.writeBytes(desc.get().run {
            """
                main=$main
                author=$author
                name=$name
                version=${version ?: project.version}
                
            """.trimIndent().toByteArray()
        })
    }
}
