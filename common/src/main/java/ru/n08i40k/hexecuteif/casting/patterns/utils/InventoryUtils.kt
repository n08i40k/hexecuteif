package ru.n08i40k.hexecuteif.casting.patterns.utils

import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.spell.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.level.block.entity.BlockEntity
import ru.n08i40k.hexecuteif.api.config.HexecuteIfConfig

sealed class InventoryWrap {
    data class Inventory(val inventory: net.minecraft.world.entity.player.Inventory) : InventoryWrap()
    data class Container(val container: net.minecraft.world.Container) : InventoryWrap()

    fun getSize(): Int {
        return when (this) {
            is Container -> this.container.containerSize
            is Inventory -> this.inventory.containerSize
        }
    }

    fun mediaMultiplier(caster: ServerPlayer): Double {
        return when (this) {
            is Inventory -> {
                if (this.inventory.player == caster) 1.0
                else HexecuteIfConfig.common().multiplyPlayerAccess()
            }

            is Container -> {
                HexecuteIfConfig.common().multiplyPlayerAccess()
            }
        }
    }

    fun assertAccess(caster: ServerPlayer) {
        when (this) {
            is Container -> {
                if (!HexecuteIfConfig.common().canAccessContainer())
                    throw MishapDisallowedSpell("can_not_access_to_container_inventory")
            }

            is Inventory -> {
                if (inventory.player != caster && !HexecuteIfConfig.common().canAccessPlayer())
                    throw MishapDisallowedSpell("can_not_access_to_player_inventory")
            }
        }
    }

    fun assertModify(caster: ServerPlayer) {
        assertAccess(caster)

        when (this) {
            is Container -> {
                if (!HexecuteIfConfig.common().canModifyContainer())
                    throw MishapDisallowedSpell("can_not_modify_container_inventory")
            }

            is Inventory -> {
                if (inventory.player != caster && !HexecuteIfConfig.common().canModifyPlayer())
                    throw MishapDisallowedSpell("can_not_modify_player_inventory")
            }
        }
    }
}

fun CastingContext.assertInventoryWrapInRange(inventoryWrap: InventoryWrap) {
    when (inventoryWrap) {
        is InventoryWrap.Container -> this.assertVecInRange((inventoryWrap.container as BlockEntity).blockPos)
        is InventoryWrap.Inventory -> this.assertEntityInRange(inventoryWrap.inventory.player)
    }
}

fun List<Iota>.getInventoryWrap(idx: Int, argc: Int, world: ServerLevel): InventoryWrap {
    val iota = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }

    if (iota is EntityIota) {
        val entity = iota.entity

        if (entity !is ServerPlayer)
            throw MishapInvalidIota.ofType(iota, if (argc == 0) idx else argc - (idx + 1), "entity.player")

        return InventoryWrap.Inventory(entity.inventory)
    } else if (iota is Vec3Iota) {
        val blockPos = BlockPos(iota.vec3)

        val blockEntity = world.getBlockEntity(blockPos)
            ?: throw MishapBadBlock(
                blockPos,
                Component.translatable("hexecuteif.mishap.excepted_block.block_entity")
            )

        if (blockEntity !is Container)
            throw MishapBadBlock(
                blockPos,
                Component.translatable("hexecuteif.mishap.excepted_block.container")
            )

        return InventoryWrap.Container(blockEntity)
    }

    throw MishapInvalidIota.of(
        iota,
        if (argc == 0) idx else argc - (idx + 1),
        "inventory_holder"
    )
}