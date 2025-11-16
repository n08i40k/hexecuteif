package ru.n08i40k.hexecuteif.casting.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.mod.HexConfig
import ru.n08i40k.hexecuteif.utils.assertInventoryWrapInRange
import ru.n08i40k.hexecuteif.utils.casterPlayer
import ru.n08i40k.hexecuteif.utils.getInventoryWrap

object OpInvOccupiedSlots : MediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): MediaAction.Result {
        val casterPlayer = env.casterPlayer

        val inventoryWrap = args.getInventoryWrap(0, argc, env.world)
        inventoryWrap.assertAccess(casterPlayer)
        env.assertInventoryWrapInRange(inventoryWrap)

        val allocatedSlots: MutableList<Iota> = mutableListOf()

        for (i in 0 until inventoryWrap.getSize()) {
            if (!inventoryWrap.getItem(i).isEmpty)
                allocatedSlots.add(DoubleIota(i.toDouble()))
        }

        return MediaAction.Result(
            allocatedSlots.asActionResult,
            (inventoryWrap.mediaMultiplier(casterPlayer) * HexConfig.common().dustMediaAmount() * 2.0).toLong()
        )
    }
}