package ru.n08i40k.hexecuteif.casting.patterns.eval

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.eval.FrameEvaluate
import at.petrak.hexcasting.api.spell.casting.eval.FrameFinishEval
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs

object OpEvalIf : Action {
    override fun operate(
        continuation: SpellContinuation,
        stack: MutableList<Iota>,
        ravenmind: Iota?,
        ctx: CastingContext
    ): OperationResult {
        if (stack.size < 2)
            throw MishapNotEnoughArgs(2, stack.size)

        val datum = stack.removeLastOrNull() ?: throw MishapNotEnoughArgs(2, 0)
        val needToEval = stack.getBool(stack.lastIndex)
        stack.removeLast()

        if (!needToEval)
            return OperationResult(continuation, stack, ravenmind, listOf())

        val instrs = evaluatable(datum, 0)

//        needToEval.

        instrs.ifRight {
            ctx.incDepth()
        }

        // if not installed already...
        // also, never make a break boundary when evaluating just one pattern
        val newCont =
            if (instrs.left().isPresent || (continuation is SpellContinuation.NotDone && continuation.frame is FrameFinishEval)) {
                continuation
            } else {
                continuation.pushFrame(FrameFinishEval) // install a break-boundary after eval
            }

        val instrsList = instrs.map({ SpellList.LList(0, listOf(PatternIota(it))) }, { it })
        val frame = FrameEvaluate(instrsList, true)
        return OperationResult(newCont.pushFrame(frame), stack, ravenmind, listOf())
    }
}