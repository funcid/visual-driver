package battlepass

import ru.cristalix.clientapi.JavaMod.clientApi

class BattlePassGuiSize {
    @JvmField var totalWidth = 550.0
    @JvmField var totalHeight = 230.0

    @JvmField var totalWidthPart = 0.0
    @JvmField var totalHeightPart = 0.0
    @JvmField var buyButtonWidth = 0.0
    @JvmField var buyButtonHeight = 0.0
    @JvmField var buyButtonOffsetX = 0.0
    @JvmField var buyButtonPickaxeOffsetX = 0.0
    @JvmField var buyButtonPickaxeOffsetY = 0.0
    @JvmField var rewardSizeX = 0.0
    @JvmField var rewardSizeY = 0.0
    @JvmField var rewardNameOffsetY = 0.0
    @JvmField var advancedOffsetY = 0.0
    @JvmField var rewardBetweenX = 0.0
    @JvmField var rewardBlockWidth = 0.0

    init {
        calculate()
    }

    fun calculate(): BattlePassGuiSize {
        val resolution = clientApi.resolution()
        totalWidth = resolution.scaledWidth_double / 1.92
        totalHeight = resolution.scaledHeight_double / 2.23

        totalWidthPart = totalWidth / 100
        totalHeightPart = totalHeight / 100

        buyButtonWidth = totalWidthPart * 25.0
        buyButtonHeight = totalHeightPart * 8.0
        buyButtonOffsetX = totalWidthPart * 3.0
        buyButtonPickaxeOffsetX = totalWidthPart * 0.8
        buyButtonPickaxeOffsetY = totalHeightPart * 0.8

        rewardSizeX = totalWidthPart * 11.45
        rewardSizeY = totalHeightPart * 18.91
        rewardNameOffsetY = totalHeightPart * 0.65

        advancedOffsetY = totalHeightPart * 20.36

        rewardBetweenX = totalWidthPart * 0.64
        rewardBlockWidth = totalWidthPart * 8.27

        return this
    }
}
