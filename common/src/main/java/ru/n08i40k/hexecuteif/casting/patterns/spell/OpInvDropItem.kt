package ru.n08i40k.hexecuteif.casting.patterns.spell

import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import ru.n08i40k.hexecuteif.casting.patterns.utils.InventoryWrap
import ru.n08i40k.hexecuteif.casting.patterns.utils.assertInventoryWrapInRange
import ru.n08i40k.hexecuteif.casting.patterns.utils.getInventoryWrap

object OpInvDropItem : SpellAction {
    override val argc: Int
        get() = 3

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val inventoryWrap = args.getInventoryWrap(0, OpInvTransferItem.argc, ctx.world)
        inventoryWrap.assertModify(ctx.caster)
        ctx.assertInventoryWrapInRange(inventoryWrap)

        val slotIdx = args.getIntBetween(1, 0, inventoryWrap.getSize(), OpInvTransferItem.argc)

        val itemCount = args.getIntBetween(2, 1, 64, OpInvTransferItem.argc)

        // validate source
        val itemStack = when (inventoryWrap) {
            is InventoryWrap.Container -> inventoryWrap.container.getItem(slotIdx)
            is InventoryWrap.Inventory -> inventoryWrap.inventory.getItem(slotIdx)
        }
        if (itemStack.isEmpty) throw MishapInvalidIota.of(
            DoubleIota(slotIdx.toDouble()), 3, "slot.not_empty", slotIdx
        )

        if (itemCount > itemStack.count)
            throw MishapInvalidIota.of(DoubleIota(slotIdx.toDouble()), 0, "double.between", 1, itemStack.count)

        val position: Vec3 = when (inventoryWrap) {
            is InventoryWrap.Container -> {
                val blockPos = (inventoryWrap.container as BlockEntity).blockPos
                blockPos.asActionResult.getVec3(0, 1)
            }
            is InventoryWrap.Inventory -> inventoryWrap.inventory.player.position()
        }
        return Triple(
            Spell(inventoryWrap, slotIdx, itemCount),
            (inventoryWrap.mediaMultiplier(ctx.caster) * HexConfig.common().shardMediaAmount()).toInt(),
            listOf(ParticleSpray.burst(position, 0.5))
        )

    }

    private data class Spell(
        val inventoryWrap: InventoryWrap,
        val slotIdx: Int,
        val itemCount: Int
    ) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            val newItemStack = when (inventoryWrap) {
                is InventoryWrap.Container -> inventoryWrap.container.getItem(slotIdx).copy()
                is InventoryWrap.Inventory -> inventoryWrap.inventory.getItem(slotIdx).copy()
            }

            when (inventoryWrap) {
                is InventoryWrap.Container -> inventoryWrap.container.getItem(slotIdx)
                is InventoryWrap.Inventory -> inventoryWrap.inventory.getItem(slotIdx)
            }.count = newItemStack.count - itemCount

            newItemStack.count = itemCount

            when (inventoryWrap) {
                is InventoryWrap.Container -> {
                    val blockPos = (inventoryWrap.container as BlockEntity).blockPos
                    val itemPos = blockPos.asActionResult.getVec3(0, 1).add(0.0, 1.0, 0.0)

                    ctx.world.addFreshEntity(ItemEntity(ctx.world, itemPos.x, itemPos.y, itemPos.z, newItemStack))
                }
                is InventoryWrap.Inventory -> {
                    inventoryWrap.inventory.player.drop(newItemStack, true)
                }
            }
        }

    }

}