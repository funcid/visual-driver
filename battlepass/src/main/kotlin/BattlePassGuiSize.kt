import ru.cristalix.clientapi.JavaMod

class BattlePassGuiSize {

    var totalWidth = 550.0
    var totalHeight = 230.0

    var totalWidthPart = 0.0
    var totalHeightPart = 0.0
    var buyButtonWidth = 0.0
    var buyButtonHeight = 0.0
    var buyButtonOffsetX = 0.0
    var buyButtonPickaxeOffsetX = 0.0
    var buyButtonPickaxeOffsetY = 0.0
    var rewardSizeX = 0.0
    var rewardSizeY = 0.0
    var rewardNameOffsetY = 0.0
    var advancedOffsetY = 0.0
    var rewardBetweenX = 0.0
    var rewardBlockWidth = 0.0

    init {
        calculate()
    }

    fun calculate(): BattlePassGuiSize {
        val resolution = JavaMod.clientApi.resolution()
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