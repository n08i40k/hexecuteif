package ru.n08i40k.hexecuteif.utils

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import net.minecraft.server.level.ServerPlayer

val CastingEnvironment.casterPlayer: ServerPlayer
    get() {
        val casterPlayer = if (this.castingEntity is ServerPlayer) this.castingEntity as ServerPlayer else null;

        if (casterPlayer == null)
            throw MishapBadCaster();

        return casterPlayer
    }