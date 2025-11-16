package ru.n08i40k.hexecuteif.casting.actions.eval

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate
import at.petrak.hexcasting.api.casting.eval.vm.FrameFinishEval
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.evaluatable
import at.petrak.hexcasting.api.casting.getBool
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds


object OpEvalIf : Action {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val stack = image.stack.toMutableList()

        if (stack.size < 2)
            throw MishapNotEnoughArgs(2, stack.size)

        val datum = stack.removeLastOrNull() ?: throw MishapNotEnoughArgs(2, 0)
        val needToEval = stack.getBool(stack.lastIndex)
        stack.removeLast()

        if (!needToEval) {
            val newImage = image.withUsedOp().copy(stack = stack)
            return OperationResult(newImage, listOf(), continuation, HexEvalSounds.MUTE)
        }

        val instrs = evaluatable(datum, 0)

        val newCont =
            if (instrs.left().isPresent || (continuation is SpellContinuation.NotDone && continuation.frame is FrameFinishEval)) {
                continuation
            } else {
                continuation.pushFrame(FrameFinishEval)
            }

        val instrsList = instrs.map({ SpellList.LList(0, listOf(it)) }, { it })
        val frame = FrameEvaluate(instrsList, true)

        val newImage = image.withUsedOp().copy(stack = stack)
        return OperationResult(newImage, listOf(), newCont.pushFrame(frame), HexEvalSounds.HERMES)
    }
}