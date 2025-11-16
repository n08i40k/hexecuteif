package ru.n08i40k.hexecuteif

import ru.n08i40k.hexecuteif.config.HexecuteIfClientConfig
import me.shedaniel.autoconfig.AutoConfig
import net.minecraft.client.gui.screens.Screen

object HexecuteIfClient {
    fun init() {
        HexecuteIfClientConfig.init()
    }

    fun getConfigScreen(parent: Screen): Screen {
        return AutoConfig.getConfigScreen(HexecuteIfClientConfig.GlobalConfig::class.java, parent).get()
    }
}
