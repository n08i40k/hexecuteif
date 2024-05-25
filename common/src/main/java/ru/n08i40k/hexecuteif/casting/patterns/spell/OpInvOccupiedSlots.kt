package ru.n08i40k.hexecuteif.casting.patterns.spell

import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import ru.n08i40k.hexecuteif.casting.patterns.MediaAction
import ru.n08i40k.hexecuteif.casting.patterns.utils.InventoryWrap
import ru.n08i40k.hexecuteif.casting.patterns.utils.getInventoryWrap
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import ru.n08i40k.hexecuteif.casting.patterns.utils.assertInventoryWrapInRange

object OpInvOccupiedSlots : MediaAction {
    override val argc: Int
        get() = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): Pair<List<Iota>, Int> {
        val inventoryWrap = args.getInventoryWrap(0, argc, ctx.world)
        inventoryWrap.assertAccess(ctx.caster)
        ctx.assertInventoryWrapInRange(inventoryWrap)

        val allocatedSlots: MutableList<Iota> = mutableListOf()

        when (inventoryWrap) {
            is InventoryWrap.Inventory -> {
                for (i in 0 until (inventoryWrap.inventory.containerSize - 5)) {
                    if (!inventoryWrap.inventory.items[i].isEmpty)
                        allocatedSlots.add(DoubleIota(i.toDouble()))
                }
            }

            is InventoryWrap.Container -> {
                for (i in 0 until inventoryWrap.container.containerSize) {
                    if (!inventoryWrap.container.getItem(i).isEmpty)
                        allocatedSlots.add(DoubleIota(i.toDouble()))
                }
            }
        }

        return Pair(
            allocatedSlots.asActionResult,
            (inventoryWrap.mediaMultiplier(ctx.caster) * HexConfig.common().dustMediaAmount() * 2.0).toInt()
        )
    }
}