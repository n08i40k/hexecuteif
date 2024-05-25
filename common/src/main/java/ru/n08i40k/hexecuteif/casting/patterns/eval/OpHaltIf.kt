package ru.n08i40k.hexecuteif.casting.patterns.eval

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation
import at.petrak.hexcasting.api.spell.iota.Iota

object OpHaltIf : Action {
    override fun operate(
        continuation: SpellContinuation,
        stack: MutableList<Iota>,
        ravenmind: Iota?,
        ctx: CastingContext
    ): OperationResult {
        val needToHalt = stack.getBool(stack.lastIndex)
        stack.removeLast()

        if (!needToHalt)
            return OperationResult(continuation, stack, ravenmind, listOf())

        var newStack = stack.toList()
        var done = false
        var newCont = continuation
        while (!done && newCont is SpellContinuation.NotDone) {
            // Kotlin Y U NO destructuring assignment
            val newInfo = newCont.frame.breakDownwards(newStack)
            done = newInfo.first
            newStack = newInfo.second
            newCont = newCont.next
        }
        // if we hit no continuation boundaries (i.e. thoth/hermes exits), we've TOTALLY cleared the itinerary...
        if (!done) {
            // bomb the stack so we exit
            newStack = listOf()
        }

        return OperationResult(newCont, newStack, ravenmind, listOf())
    }
}