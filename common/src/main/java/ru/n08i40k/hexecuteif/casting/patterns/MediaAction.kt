package ru.n08i40k.hexecuteif.casting.patterns

import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.OperationResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs

interface MediaAction : Action {
    val argc: Int

    fun execute(args: List<Iota>, ctx: CastingContext): Pair<List<Iota>, Int>

    override fun operate(
        continuation: SpellContinuation,
        stack: MutableList<Iota>,
        ravenmind: Iota?,
        ctx: CastingContext
    ): OperationResult {
        if (this.argc > stack.size)
            throw MishapNotEnoughArgs(this.argc, stack.size)

        val args = stack.takeLast(this.argc)
        repeat(this.argc) { stack.removeLast(); }

        val result = this.execute(args, ctx)
        stack.addAll(result.first)

        val sideEffects = mutableListOf<OperatorSideEffect>(OperatorSideEffect.ConsumeMedia(result.second))
        return OperationResult(continuation, stack, ravenmind, sideEffects)
    }
}