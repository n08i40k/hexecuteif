package ru.n08i40k.hexecuteif

import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import ru.n08i40k.hexecuteif.config.HexecuteIfServerConfig
import ru.n08i40k.hexecuteif.networking.HexecuteIfNetworking
import ru.n08i40k.hexecuteif.registry.HexecuteIfActions

object HexecuteIf {
    const val MODID = "hexecuteif"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(MODID)

    @JvmStatic
    fun id(path: String) = ResourceLocation(MODID, path)

    fun init() {
        HexecuteIfServerConfig.init()
        initRegistries(
            HexecuteIfActions,
        )
        HexecuteIfNetworking.init()
    }

    fun initServer() {
        HexecuteIfServerConfig.initServer()
    }
}
