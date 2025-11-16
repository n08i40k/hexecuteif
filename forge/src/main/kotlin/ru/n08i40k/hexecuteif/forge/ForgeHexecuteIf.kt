package ru.n08i40k.hexecuteif.forge

import dev.architectury.platform.forge.EventBuses
import ru.n08i40k.hexecuteif.HexecuteIf
import ru.n08i40k.hexecuteif.forge.datagen.ForgeHexecuteIfDatagen
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(HexecuteIf.MODID)
class ForgeHexecuteIf {
    init {
        MOD_BUS.apply {
            EventBuses.registerModEventBus(HexecuteIf.MODID, this)
            addListener(ForgeHexecuteIfClient::init)
            addListener(ForgeHexecuteIfDatagen::init)
            addListener(ForgeHexecuteIfServer::init)
        }
        HexecuteIf.init()
    }
}
