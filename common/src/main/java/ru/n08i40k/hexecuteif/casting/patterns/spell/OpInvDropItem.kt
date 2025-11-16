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
        val inventoryWrap = args.getInventoryWrap(0, argc, ctx.world)
        inventoryWrap.assertModify(ctx.caster)

        ctx.assertInventoryWrapInRange(inventoryWrap)

        val slotIdx = args.getIntBetween(1, 0, inventoryWrap.getSize(), argc)

        val itemCount = args.getIntBetween(2, 1, 64, argc)

        // validate source
        val itemStack = inventoryWrap.getItem(slotIdx)

        if (itemStack.isEmpty) throw MishapInvalidIota.of(
            DoubleIota(slotIdx.toDouble()), 1, "slot.not_empty", slotIdx
        )

        if (itemCount > itemStack.count)
            throw MishapInvalidIota.of(
                DoubleIota(slotIdx.toDouble()), 0, "double.between", 1, itemStack.count
            )

        val position: Vec3 = when (inventoryWrap) {
            is InventoryWrap.Container -> {
                val blockPos = (inventoryWrap.container as BlockEntity).blockPos
                Vec3(blockPos.x.toDouble(), blockPos.y + 1.0, blockPos.z.toDouble())
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
            val itemStack = inventoryWrap.getItem(slotIdx)
            val newItemStack = itemStack.copy()

            itemStack.count = newItemStack.count - itemCount
            newItemStack.count = itemCount

            when (inventoryWrap) {
                is InventoryWrap.Container -> {
                    val blockPos = (inventoryWrap.container as BlockEntity).blockPos

                    val itemEntity = ItemEntity(
                        ctx.world,
                        blockPos.x.toDouble(),
                        blockPos.y + 1.0,
                        blockPos.z.toDouble(),
                        newItemStack
                    )

                    ctx.world.addFreshEntity(itemEntity)
                }

                is InventoryWrap.Inventory -> inventoryWrap.inventory.player.drop(newItemStack, true)
            }
        }

    }

}