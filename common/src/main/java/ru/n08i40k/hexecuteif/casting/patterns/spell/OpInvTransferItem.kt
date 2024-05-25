package ru.n08i40k.hexecuteif.casting.patterns.spell

import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import ru.n08i40k.hexecuteif.casting.patterns.utils.InventoryWrap
import ru.n08i40k.hexecuteif.casting.patterns.utils.assertInventoryWrapInRange
import ru.n08i40k.hexecuteif.casting.patterns.utils.getInventoryWrap

object OpInvTransferItem : SpellAction {
    override val argc: Int
        get() = 5

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val sourceInventoryWrap = args.getInventoryWrap(0, argc, ctx.world)
        sourceInventoryWrap.assertModify(ctx.caster)
        ctx.assertInventoryWrapInRange(sourceInventoryWrap)

        val targetInventoryWrap = args.getInventoryWrap(2, argc, ctx.world)
        targetInventoryWrap.assertModify(ctx.caster)
        ctx.assertInventoryWrapInRange(targetInventoryWrap)

        val sourceSlotIdx = args.getIntBetween(1, 0, sourceInventoryWrap.getSize(), argc)
        val targetSlotIdx = args.getIntBetween(3, 0, targetInventoryWrap.getSize(), argc)

        val itemCount = args.getIntBetween(4, 1, 64, argc)

        // validate source
        val sourceItemStack = when (sourceInventoryWrap) {
            is InventoryWrap.Container -> sourceInventoryWrap.container.getItem(sourceSlotIdx)
            is InventoryWrap.Inventory -> sourceInventoryWrap.inventory.getItem(sourceSlotIdx)
        }
        if (sourceItemStack.isEmpty) throw MishapInvalidIota.of(
            DoubleIota(sourceSlotIdx.toDouble()), 3, "slot.not_empty", sourceSlotIdx
        )

        if (itemCount > sourceItemStack.count)
            throw MishapInvalidIota.of(DoubleIota(sourceSlotIdx.toDouble()), 0, "double.between", 1, sourceItemStack.count)

        // validate target
        val targetItemStackEmpty: Boolean = when (targetInventoryWrap) {
            is InventoryWrap.Container -> targetInventoryWrap.container.getItem(targetSlotIdx).isEmpty
            is InventoryWrap.Inventory -> targetInventoryWrap.inventory.getItem(targetSlotIdx).isEmpty
        }

        if (!targetItemStackEmpty) throw MishapInvalidIota.of(
            DoubleIota(sourceSlotIdx.toDouble()),
            1,
            "slot.empty",
            targetSlotIdx,
            sourceItemStack.displayName
        )

        val position: Vec3 = when (sourceInventoryWrap) {
            is InventoryWrap.Container -> {
                val blockPos = (sourceInventoryWrap.container as BlockEntity).blockPos
                blockPos.asActionResult.getVec3(0, 1)
            }

            is InventoryWrap.Inventory -> sourceInventoryWrap.inventory.player.position()
        }

        val mediaAmount = HexConfig.common().shardMediaAmount();
        val amounts =
            sourceInventoryWrap.mediaMultiplier(ctx.caster) * mediaAmount + targetInventoryWrap.mediaMultiplier(ctx.caster) * mediaAmount

        return Triple(
            Spell(sourceInventoryWrap, sourceSlotIdx, targetInventoryWrap, targetSlotIdx, itemCount),
            (amounts * 2).toInt(),
            listOf(ParticleSpray.burst(position, 0.5))
        )
    }

    private data class Spell(
        val sourceInventoryWrap: InventoryWrap,
        val sourceSlotIdx: Int,
        val targetInventoryWrap: InventoryWrap,
        val targetSlotIdx: Int,
        val itemCount: Int
    ) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            val sourceItemStack = when (sourceInventoryWrap) {
                is InventoryWrap.Container -> sourceInventoryWrap.container.getItem(sourceSlotIdx).copy()
                is InventoryWrap.Inventory -> sourceInventoryWrap.inventory.getItem(sourceSlotIdx).copy()
            }

            when (sourceInventoryWrap) {
                is InventoryWrap.Container -> sourceInventoryWrap.container.getItem(sourceSlotIdx)
                is InventoryWrap.Inventory -> sourceInventoryWrap.inventory.getItem(sourceSlotIdx)
            }.count = sourceItemStack.count - itemCount

            sourceItemStack.count = itemCount

            when (targetInventoryWrap) {
                is InventoryWrap.Container -> targetInventoryWrap.container.setItem(targetSlotIdx, sourceItemStack)
                is InventoryWrap.Inventory -> targetInventoryWrap.inventory.setItem(targetSlotIdx, sourceItemStack)
            }
        }

    }
}