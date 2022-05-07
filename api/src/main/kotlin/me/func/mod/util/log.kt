package me.func.mod.util

const val ANSI_RESET = "\u001B[0m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_YELLOW = "\u001B[33m"

fun warn(message: String) = println("$ANSI_YELLOW[ANIME] $message$ANSI_RESET")

fun log(message: String) = println("$ANSI_GREEN[ANIME] $message$ANSI_RESET")