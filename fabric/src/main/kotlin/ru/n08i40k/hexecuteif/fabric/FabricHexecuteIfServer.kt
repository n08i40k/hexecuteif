package ru.n08i40k.hexecuteif.fabric

import ru.n08i40k.hexecuteif.HexecuteIf
import net.fabricmc.api.DedicatedServerModInitializer

object FabricHexecuteIfServer : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        HexecuteIf.initServer()
    }
}
