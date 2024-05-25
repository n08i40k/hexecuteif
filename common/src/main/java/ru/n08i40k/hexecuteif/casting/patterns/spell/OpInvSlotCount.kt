package ru.n08i40k.hexecuteif.casting.patterns.spell

import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import ru.n08i40k.hexecuteif.casting.patterns.MediaAction
import ru.n08i40k.hexecuteif.casting.patterns.utils.InventoryWrap
import ru.n08i40k.hexecuteif.casting.patterns.utils.assertInventoryWrapInRange
import ru.n08i40k.hexecuteif.casting.patterns.utils.getInventoryWrap

object OpInvSlotCount : MediaAction {
    override val argc: Int
        get() = 1

    override fun execute(args: List<Iota>, ctx: CastingContext): Pair<List<Iota>, Int> {
        val inventoryWrap = args.getInventoryWrap(0, argc, ctx.world)
        inventoryWrap.assertAccess(ctx.caster)
        ctx.assertInventoryWrapInRange(inventoryWrap)

        val slotCount = when (inventoryWrap) {
            is InventoryWrap.Inventory -> {
                inventoryWrap.inventory.containerSize
            }

            is InventoryWrap.Container -> {
                inventoryWrap.container.containerSize
            }
        }

        return Pair(
            slotCount.asActionResult,
            (inventoryWrap.mediaMultiplier(ctx.caster) * HexConfig.common().dustMediaAmount()).toInt()
        )
    }
}