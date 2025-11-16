package ru.n08i40k.hexecuteif.casting.actions

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughMedia
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import net.minecraft.nbt.CompoundTag

interface MediaAction : Action {
    data class Result(
        val iotas: List<Iota>,
        val cost: Long,
    )

    val argc: Int

    fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): Result

    fun executeWithUserdata(
        args: List<Iota>,
        env: CastingEnvironment,
        userData: CompoundTag
    ): Result {
        return this.execute(args, env)
    }

    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val stack = image.stack.toMutableList()

        if (this.argc > stack.size)
            throw MishapNotEnoughArgs(this.argc, stack.size)

        val args = stack.takeLast(this.argc)
        repeat(this.argc) { stack.removeLast(); }

        val userDataMut = image.userData.copy()
        val result = this.executeWithUserdata(args, env, userDataMut)

        stack.addAll(result.iotas)

        val sideEffects = mutableListOf<OperatorSideEffect>()

        if (env.extractMedia(result.cost, true) > 0)
            throw MishapNotEnoughMedia(result.cost)

        if (result.cost > 0)
            sideEffects.add(OperatorSideEffect.ConsumeMedia(result.cost))

        val newImage = image.copy(stack = stack.toList(), userData = userDataMut, opsConsumed = image.opsConsumed + 1);

        return OperationResult(newImage, sideEffects, continuation, HexEvalSounds.MUTE)
    }
}