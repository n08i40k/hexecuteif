package ru.n08i40k.hexecuteif.casting.actions.spells

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.mod.HexConfig
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import ru.n08i40k.hexecuteif.utils.InventoryWrap
import ru.n08i40k.hexecuteif.utils.assertInventoryWrapInRange
import ru.n08i40k.hexecuteif.utils.casterPlayer
import ru.n08i40k.hexecuteif.utils.getInventoryWrap

object OpInvTransferItem : SpellAction {
    override val argc = 5

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val casterPlayer = env.casterPlayer

        val sourceInventoryWrap = args.getInventoryWrap(0, argc, env.world)
        sourceInventoryWrap.assertModify(casterPlayer)
        env.assertInventoryWrapInRange(sourceInventoryWrap)

        val targetInventoryWrap = args.getInventoryWrap(2, argc, env.world)
        targetInventoryWrap.assertModify(casterPlayer)
        env.assertInventoryWrapInRange(targetInventoryWrap)

        val sourceSlotIdx = args.getIntBetween(1, 0, sourceInventoryWrap.getSize(), argc)
        val targetSlotIdx = args.getIntBetween(3, 0, targetInventoryWrap.getSize(), argc)

        val itemCount = args.getIntBetween(4, 1, 64, argc)

        // validate source
        val sourceItemStack = sourceInventoryWrap.getItem(sourceSlotIdx)
        if (sourceItemStack.isEmpty) throw MishapInvalidIota.of(
            DoubleIota(sourceSlotIdx.toDouble()), 3, "slot.not_empty", sourceSlotIdx
        )

        if (itemCount > sourceItemStack.count)
            throw MishapInvalidIota.of(
                DoubleIota(sourceSlotIdx.toDouble()), 0, "double.between", 1, sourceItemStack.count
            )

        // validate target
        val targetItemStack = targetInventoryWrap.getItem(targetSlotIdx)
        if (!targetItemStack.isEmpty) throw MishapInvalidIota.of(
            DoubleIota(sourceSlotIdx.toDouble()),
            1,
            "slot.empty",
            targetSlotIdx,
            targetItemStack.displayName
        )

        val position: Vec3 = when (sourceInventoryWrap) {
            is InventoryWrap.Container -> {
                val blockPos = (sourceInventoryWrap.container as BlockEntity).blockPos
                blockPos.asActionResult.getVec3(0, 1)
            }

            is InventoryWrap.Inventory -> sourceInventoryWrap.inventory.player.position()
        }

        val mediaAmount = HexConfig.common().shardMediaAmount()
        val amounts =
            sourceInventoryWrap.mediaMultiplier(casterPlayer) * mediaAmount + targetInventoryWrap.mediaMultiplier(
                casterPlayer
            ) * mediaAmount

        return SpellAction.Result(
            Spell(sourceInventoryWrap, sourceSlotIdx, targetInventoryWrap, targetSlotIdx, itemCount),
            (amounts * 2).toLong(),
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
        override fun cast(env: CastingEnvironment) {
            val sourceItemStack = sourceInventoryWrap.getItem(sourceSlotIdx)
            val newItemStack = sourceItemStack.copy()

            sourceItemStack.count = newItemStack.count - itemCount
            newItemStack.count = itemCount

            when (targetInventoryWrap) {
                is InventoryWrap.Container -> targetInventoryWrap.container.setItem(targetSlotIdx, newItemStack)
                is InventoryWrap.Inventory -> {
                    val targetInventory = targetInventoryWrap.inventory

                    if (targetSlotIdx == 36)
                        targetInventory.offhand[0] = newItemStack
                    else if (targetSlotIdx > 36)
                        targetInventory.armor[targetSlotIdx - 37] = newItemStack
                    else
                        targetInventory.setItem(targetSlotIdx, newItemStack)
                }
            }
        }

    }
}