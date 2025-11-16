package ru.n08i40k.hexecuteif.networking.msg

import dev.architectury.networking.NetworkChannel
import dev.architectury.networking.NetworkManager.PacketContext
import ru.n08i40k.hexecuteif.HexecuteIf
import ru.n08i40k.hexecuteif.networking.HexecuteIfNetworking
import ru.n08i40k.hexecuteif.networking.handler.applyOnClient
import ru.n08i40k.hexecuteif.networking.handler.applyOnServer
import net.fabricmc.api.EnvType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import java.util.function.Supplier

sealed interface HexecuteIfMessage

sealed interface HexecuteIfMessageC2S : HexecuteIfMessage {
    fun sendToServer() {
        HexecuteIfNetworking.CHANNEL.sendToServer(this)
    }
}

sealed interface HexecuteIfMessageS2C : HexecuteIfMessage {
    fun sendToPlayer(player: ServerPlayer) {
        HexecuteIfNetworking.CHANNEL.sendToPlayer(player, this)
    }

    fun sendToPlayers(players: Iterable<ServerPlayer>) {
        HexecuteIfNetworking.CHANNEL.sendToPlayers(players, this)
    }
}

sealed interface HexecuteIfMessageCompanion<T : HexecuteIfMessage> {
    val type: Class<T>

    fun decode(buf: FriendlyByteBuf): T

    fun T.encode(buf: FriendlyByteBuf)

    fun apply(msg: T, supplier: Supplier<PacketContext>) {
        val ctx = supplier.get()
        when (ctx.env) {
            EnvType.SERVER, null -> {
                HexecuteIf.LOGGER.debug("Server received packet from {}: {}", ctx.player.name.string, this)
                when (msg) {
                    is HexecuteIfMessageC2S -> msg.applyOnServer(ctx)
                    else -> HexecuteIf.LOGGER.warn("Message not handled on server: {}", msg::class)
                }
            }
            EnvType.CLIENT -> {
                HexecuteIf.LOGGER.debug("Client received packet: {}", this)
                when (msg) {
                    is HexecuteIfMessageS2C -> msg.applyOnClient(ctx)
                    else -> HexecuteIf.LOGGER.warn("Message not handled on client: {}", msg::class)
                }
            }
        }
    }

    fun register(channel: NetworkChannel) {
        channel.register(type, { msg, buf -> msg.encode(buf) }, ::decode, ::apply)
    }
}
