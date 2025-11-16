package ru.n08i40k.hexecuteif.networking.handler

import dev.architectury.networking.NetworkManager.PacketContext
import ru.n08i40k.hexecuteif.config.HexecuteIfServerConfig
import ru.n08i40k.hexecuteif.networking.msg.*

fun HexecuteIfMessageS2C.applyOnClient(ctx: PacketContext) = ctx.queue {
    when (this) {
        is MsgSyncConfigS2C -> {
            HexecuteIfServerConfig.onSyncConfig(serverConfig)
        }

        // add more client-side message handlers here
    }
}
