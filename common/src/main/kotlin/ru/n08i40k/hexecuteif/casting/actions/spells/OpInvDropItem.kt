package ru.n08i40k.hexecuteif.casting.actions.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getIntBetween
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.mod.HexConfig
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import ru.n08i40k.hexecuteif.utils.InventoryWrap
import ru.n08i40k.hexecuteif.utils.assertInventoryWrapInRange
import ru.n08i40k.hexecuteif.utils.casterPlayer
import ru.n08i40k.hexecuteif.utils.getInventoryWrap

object OpInvDropItem : SpellAction {
    override val argc = 3

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val casterPlayer = env.casterPlayer

        val inventoryWrap = args.getInventoryWrap(0, argc, env.world)
        inventoryWrap.assertModify(casterPlayer)

        env.assertInventoryWrapInRange(inventoryWrap)

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

        return SpellAction.Result(
            Spell(inventoryWrap, slotIdx, itemCount),
            (inventoryWrap.mediaMultiplier(casterPlayer) * HexConfig.common().shardMediaAmount()).toLong(),
            listOf(ParticleSpray.burst(position, 0.5))
        )
    }

    private data class Spell(
        val inventoryWrap: InventoryWrap,
        val slotIdx: Int,
        val itemCount: Int
    ) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val itemStack = inventoryWrap.getItem(slotIdx)
            val newItemStack = itemStack.copy()

            itemStack.count = newItemStack.count - itemCount
            newItemStack.count = itemCount

            when (inventoryWrap) {
                is InventoryWrap.Container -> {
                    val blockPos = (inventoryWrap.container as BlockEntity).blockPos

                    val itemEntity = ItemEntity(
                        env.world,
                        blockPos.x.toDouble(),
                        blockPos.y + 1.0,
                        blockPos.z.toDouble(),
                        newItemStack
                    )

                    env.world.addFreshEntity(itemEntity)
                }

                is InventoryWrap.Inventory -> inventoryWrap.inventory.player.drop(newItemStack, true)
            }
        }

    }

}