package me.func.mod.util

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

fun dir(directory: String) = Paths.get(directory).apply {
    if (!Files.exists(this) || !Files.isDirectory(this))
        Files.createDirectory(this)
}

fun String.fileLastName() = split("/").last()

fun Path.fileLastName() = absolutePathString().fileLastName()