package me.func.protocol.booster.bar

data class OpenBoosterRequest(
    var segments: List<String>,
    var title: String,
    var subtitle: String,
    var isShowBackground: Boolean,
    var progress: Double
) {
    

    constructor() : this(listOf(), "", "", false, 1.0)
}