package ru.n08i40k.hexecuteif.casting.patterns.spell

import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getInt
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapError
import ru.n08i40k.hexecuteif.casting.patterns.MediaAction
import ru.n08i40k.hexecuteif.casting.patterns.utils.assertInventoryWrapInRange
import ru.n08i40k.hexecuteif.casting.patterns.utils.getInventoryWrap

object OpInvSlotItemCount : MediaAction {
    override val argc: Int
        get() = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): Pair<List<Iota>, Int> {
        val inventoryWrap = args.getInventoryWrap(0, argc, ctx.world)
        inventoryWrap.assertAccess(ctx.caster)
        ctx.assertInventoryWrapInRange(inventoryWrap)

        val slotIdx = args.getInt(1, argc)

        if (slotIdx < 0 || slotIdx >= inventoryWrap.getSize())
            throw MishapError(IndexOutOfBoundsException(slotIdx))

        val itemStack = inventoryWrap.getItem(slotIdx)

        val count = if (itemStack.isEmpty) 0 else itemStack.count

        return Pair(
            count.toDouble().asActionResult,
            (inventoryWrap.mediaMultiplier(ctx.caster) * HexConfig.common().dustMediaAmount()).toInt()
        )
    }

}