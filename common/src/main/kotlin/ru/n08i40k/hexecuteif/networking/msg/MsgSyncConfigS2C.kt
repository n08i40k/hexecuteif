package ru.n08i40k.hexecuteif.networking.msg

import ru.n08i40k.hexecuteif.config.HexecuteIfServerConfig
import net.minecraft.network.FriendlyByteBuf

data class MsgSyncConfigS2C(val serverConfig: HexecuteIfServerConfig.ServerConfig) : HexecuteIfMessageS2C {
    companion object : HexecuteIfMessageCompanion<MsgSyncConfigS2C> {
        override val type = MsgSyncConfigS2C::class.java

        override fun decode(buf: FriendlyByteBuf) = MsgSyncConfigS2C(
            serverConfig = HexecuteIfServerConfig.ServerConfig().decode(buf),
        )

        override fun MsgSyncConfigS2C.encode(buf: FriendlyByteBuf) {
            serverConfig.encode(buf)
        }
    }
}
