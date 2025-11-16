package ru.n08i40k.hexecuteif.casting.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidOperatorArgs
import at.petrak.hexcasting.api.mod.HexConfig
import ru.n08i40k.hexecuteif.utils.assertInventoryWrapInRange
import ru.n08i40k.hexecuteif.utils.casterPlayer
import ru.n08i40k.hexecuteif.utils.getInventoryWrap

object OpInvSlotItemCount : MediaAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): MediaAction.Result {
        val casterPlayer = env.casterPlayer

        val inventoryWrap = args.getInventoryWrap(0, argc, env.world)
        inventoryWrap.assertAccess(casterPlayer)
        env.assertInventoryWrapInRange(inventoryWrap)

        val slotIdx = args.getInt(1, argc)

        if (slotIdx < 0 || slotIdx >= inventoryWrap.getSize())
            throw MishapInvalidOperatorArgs(args)

        val itemStack = inventoryWrap.getItem(slotIdx)

        val count = if (itemStack.isEmpty) 0 else itemStack.count

        return MediaAction.Result(
            count.toDouble().asActionResult,
            (inventoryWrap.mediaMultiplier(casterPlayer) * HexConfig.common().dustMediaAmount()).toLong()
        )
    }

}