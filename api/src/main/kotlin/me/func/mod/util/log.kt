package me.func.mod.util

import net.minecrell.terminalconsole.TerminalConsoleAppender

private val ANSI_SUPPORTED: Boolean = TerminalConsoleAppender.isAnsiSupported()
private val ANSI_RESET = if (ANSI_SUPPORTED) "\u001B[0m" else ""
private val ANSI_GREEN = if (ANSI_SUPPORTED) "\u001B[32m" else ""
private val ANSI_YELLOW = if (ANSI_SUPPORTED) "\u001B[33m" else ""

fun warn(message: String): Unit = println("$ANSI_YELLOW[ANIME] $message$ANSI_RESET")

fun log(message: String): Unit = println("$ANSI_GREEN[ANIME] $message$ANSI_RESET")
