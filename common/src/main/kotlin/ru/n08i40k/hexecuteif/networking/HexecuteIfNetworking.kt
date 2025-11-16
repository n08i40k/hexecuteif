package ru.n08i40k.hexecuteif.networking

import dev.architectury.networking.NetworkChannel
import ru.n08i40k.hexecuteif.HexecuteIf
import ru.n08i40k.hexecuteif.networking.msg.HexecuteIfMessageCompanion

object HexecuteIfNetworking {
    val CHANNEL: NetworkChannel = NetworkChannel.create(HexecuteIf.id("networking_channel"))

    fun init() {
        for (subclass in HexecuteIfMessageCompanion::class.sealedSubclasses) {
            subclass.objectInstance?.register(CHANNEL)
        }
    }
}
