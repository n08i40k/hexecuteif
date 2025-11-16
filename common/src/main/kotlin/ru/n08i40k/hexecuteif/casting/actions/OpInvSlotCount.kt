package ru.n08i40k.hexecuteif.casting.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.mod.HexConfig
import ru.n08i40k.hexecuteif.utils.assertInventoryWrapInRange
import ru.n08i40k.hexecuteif.utils.casterPlayer
import ru.n08i40k.hexecuteif.utils.getInventoryWrap

object OpInvSlotCount : MediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): MediaAction.Result {
        val casterPlayer = env.casterPlayer

        val inventoryWrap = args.getInventoryWrap(0, argc, env.world)
        inventoryWrap.assertAccess(casterPlayer)
        env.assertInventoryWrapInRange(inventoryWrap)

        val slotCount = inventoryWrap.getSize()

        return MediaAction.Result(
            slotCount.asActionResult,
            (inventoryWrap.mediaMultiplier(casterPlayer) * HexConfig.common().dustMediaAmount()).toLong()
        )
    }
}