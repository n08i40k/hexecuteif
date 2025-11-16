package ru.n08i40k.hexecuteif.forge

import ru.n08i40k.hexecuteif.HexecuteIf
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent

object ForgeHexecuteIfServer {
    @Suppress("UNUSED_PARAMETER")
    fun init(event: FMLDedicatedServerSetupEvent) {
        HexecuteIf.initServer()
    }
}
