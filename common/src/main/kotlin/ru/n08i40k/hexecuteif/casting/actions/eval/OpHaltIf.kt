package ru.n08i40k.hexecuteif.casting.actions.eval

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.getBool
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds


object OpHaltIf : Action {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val stack = image.stack.toMutableList()

        val needToHalt = stack.getBool(stack.lastIndex)
        stack.removeLast()

        if (!needToHalt) {
            val newImage = image.withUsedOp().copy(stack = stack)
            return OperationResult(newImage, listOf(), continuation, HexEvalSounds.HERMES)
        }

        var newStack = stack.toList()

        var done = false
        var newCont = continuation
        while (!done && newCont is SpellContinuation.NotDone) {
            val newInfo = newCont.frame.breakDownwards(newStack)
            done = newInfo.first
            newStack = newInfo.second
            newCont = newCont.next
        }

        if (!done) newStack = listOf()

        val newImage = image.withUsedOp().copy(stack = newStack)
        return OperationResult(newImage, listOf(), continuation, HexEvalSounds.SPELL)
    }
}