rootProject.name = "animation-api"

arrayOf(
    "api",
    "mod",
    "protocol",
    "experimental",
    "graffiti",
    "graffiti-protocol",
    "graffiti-service",
    "npc",
    "battlepass",
    "dialog",
    "lootbox",
    "protocol-serialization",
    "protocol-mod"
).forEach { include(":$it") }
