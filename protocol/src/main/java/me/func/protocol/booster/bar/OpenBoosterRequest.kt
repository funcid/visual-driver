package me.func.protocol.booster.bar

data class OpenBoosterRequest(
    val segments: List<String>,
    val title: String,
    val subtitle: String,
    val isShowBackground: Boolean,
    val progress: Double
)