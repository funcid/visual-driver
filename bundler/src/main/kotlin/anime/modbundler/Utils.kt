package anime

import java.io.File
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * Idea moment (disable unused main class)
 */
annotation class GradlePlugin

private val temp = Files.createTempDirectory("modbundler").apply { toFile().deleteOnExit() }

fun KClass<*>.getResource(path: String): File =
    temp.resolve("${System.currentTimeMillis() + Random.nextInt()}.pro").also {
        Files.copy(java.getResourceAsStream(path)!!, it)
    }.toFile()
