package me.func.mod.util

import me.func.mod.Anime
import org.bukkit.entity.Player

fun Player.openUrl(url: String) = Anime.openUrl(this, url)

fun Player.openP13n() = Anime.openP13n(this)

fun Player.title(text: String) = Anime.title(this, text)

fun Player.title(vararg text: String) = Anime.title(this, *text)

fun Player.killboard(text: String) = Anime.killboardMessage(this, text)

fun Player.loadTexture(url: String) = Anime.loadTexture(this, url)
