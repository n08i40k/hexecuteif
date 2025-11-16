package ru.n08i40k.hexecuteif.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import ru.n08i40k.hexecuteif.HexecuteIfClient

object FabricHexecuteIfModMenu : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory(HexecuteIfClient::getConfigScreen)
}
