package ru.n08i40k.hexecuteif.fabric

import ru.n08i40k.hexecuteif.HexecuteIf
import net.fabricmc.api.ModInitializer

object FabricHexecuteIf : ModInitializer {
    override fun onInitialize() {
        HexecuteIf.init()
    }
}
