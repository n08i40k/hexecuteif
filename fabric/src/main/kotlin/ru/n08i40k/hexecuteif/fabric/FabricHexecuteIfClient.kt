package ru.n08i40k.hexecuteif.fabric

import ru.n08i40k.hexecuteif.HexecuteIfClient
import net.fabricmc.api.ClientModInitializer

object FabricHexecuteIfClient : ClientModInitializer {
    override fun onInitializeClient() {
        HexecuteIfClient.init()
    }
}
