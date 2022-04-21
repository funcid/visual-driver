package me.func.mod

import java.util.logging.Level

fun log(message: String, level: Level = Level.WARNING) = Anime.provided.logger.log(level, message)