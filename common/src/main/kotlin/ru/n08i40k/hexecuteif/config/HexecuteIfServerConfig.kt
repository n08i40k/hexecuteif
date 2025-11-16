package ru.n08i40k.hexecuteif.config

import dev.architectury.event.events.common.PlayerEvent
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.ConfigHolder
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject
import me.shedaniel.autoconfig.serializer.PartitioningSerializer
import me.shedaniel.autoconfig.serializer.PartitioningSerializer.GlobalData
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.InteractionResult
import ru.n08i40k.hexecuteif.HexecuteIf
import ru.n08i40k.hexecuteif.networking.msg.MsgSyncConfigS2C

object HexecuteIfServerConfig {
    @JvmStatic
    lateinit var holder: ConfigHolder<GlobalConfig>

    @JvmStatic
    val config get() = syncedServerConfig ?: holder.config.server

    // only used on the client
    private var syncedServerConfig: ServerConfig? = null

    fun init() {
        holder = AutoConfig.register(
            GlobalConfig::class.java,
            PartitioningSerializer.wrap(::Toml4jConfigSerializer),
        )

        // prevent this holder from saving the server config; that happens in the client config gui
        holder.registerSaveListener { _, _ -> InteractionResult.FAIL }
    }

    fun initServer() {
        PlayerEvent.PLAYER_JOIN.register { player ->
            MsgSyncConfigS2C(holder.config.server).sendToPlayer(player)
        }
    }

    fun onSyncConfig(serverConfig: ServerConfig?) {
        syncedServerConfig = serverConfig
    }

    @Config(name = HexecuteIf.MODID)
    class GlobalConfig(
        @Category("server")
        @TransitiveObject
        val server: ServerConfig = ServerConfig(),
    ) : GlobalData()

    @Config(name = "server")
    class ServerConfig : ConfigData {
        @Tooltip
        var multiplyContainerAccess: Double = 1.5
            private set

        @Tooltip
        var multiplyPlayerAccess: Double = 5.0
            private set

        @Tooltip
        var canAccessContainer: Boolean = true
            private set

        @Tooltip
        var canAccessPlayer: Boolean = true
            private set

        @Tooltip
        var canModifyContainer: Boolean = true
            private set

        @Tooltip
        var canModifyPlayer: Boolean = true
            private set

        fun encode(buf: FriendlyByteBuf) {
            buf.writeDouble(multiplyContainerAccess)
            buf.writeDouble(multiplyPlayerAccess)

            buf.writeBoolean(canAccessContainer)
            buf.writeBoolean(canAccessPlayer)

            buf.writeBoolean(canModifyContainer)
            buf.writeBoolean(canModifyPlayer)
        }

        fun decode(buf: FriendlyByteBuf): ServerConfig {
            multiplyContainerAccess = buf.readDouble()
            multiplyPlayerAccess = buf.readDouble()

            canAccessContainer = buf.readBoolean()
            canAccessPlayer = buf.readBoolean()

            canModifyContainer = buf.readBoolean()
            canModifyPlayer = buf.readBoolean()

            return this
        }
    }
}
