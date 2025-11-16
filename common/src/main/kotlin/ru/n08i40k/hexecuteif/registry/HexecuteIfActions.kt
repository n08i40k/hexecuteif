package ru.n08i40k.hexecuteif.registry

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexActions
import ru.n08i40k.hexecuteif.casting.actions.OpCanBreakBlock
import ru.n08i40k.hexecuteif.casting.actions.OpCanPlaceBlock
import ru.n08i40k.hexecuteif.casting.actions.OpInvOccupiedSlots
import ru.n08i40k.hexecuteif.casting.actions.OpInvSlotCount
import ru.n08i40k.hexecuteif.casting.actions.OpInvSlotItemCount
import ru.n08i40k.hexecuteif.casting.actions.eval.OpEvalIf
import ru.n08i40k.hexecuteif.casting.actions.eval.OpHaltIf
import ru.n08i40k.hexecuteif.casting.actions.spells.OpInvDropItem
import ru.n08i40k.hexecuteif.casting.actions.spells.OpInvTransferItem

@Suppress("unused")
object HexecuteIfActions : HexecuteIfRegistrar<ActionRegistryEntry>(
    HexRegistries.ACTION,
    { HexActions.REGISTRY },
) {
    val EVAL_IF = make("eval_if", HexDir.SOUTH_EAST, "deaqqq", OpEvalIf)
    val HALT_IF = make("halt_if", HexDir.SOUTH_WEST, "aqdeee", OpHaltIf)

    val CAN_BREAK_BLOCK = make("can_break_block", HexDir.EAST, "qaqqqqqwa", OpCanBreakBlock)
    val CAN_PLACE_BLOCK = make("can_place_block", HexDir.SOUTH_EAST, "eeeeedeed", OpCanPlaceBlock)

    val INV_SLOT_COUNT = make("inv_slot_count", HexDir.SOUTH_EAST, "qwawqwaqw", OpInvSlotCount)
    val INV_OCCUPIED_SLOTS = make("inv_occupied_slots", HexDir.NORTH_WEST, "dwewdwed", OpInvOccupiedSlots)
    val INV_SLOT_ITEM_COUNT = make("inv_slot_item_count", HexDir.NORTH_WEST, "dwewdweedq", OpInvSlotItemCount)

    val INV_DROP_ITEM = make("inv_drop_item", HexDir.NORTH_WEST, "dwewdwedwwadeeed", OpInvDropItem)
    val INV_TRANSFER_ITEM = make("inv_transfer_item", HexDir.NORTH_WEST, "dwewdwedwwadeeedewadeeed", OpInvTransferItem)

    private fun make(name: String, startDir: HexDir, signature: String, action: Action) =
        make(name, startDir, signature) { action }

    private fun make(name: String, startDir: HexDir, signature: String, getAction: () -> Action) = register(name) {
        ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), getAction())
    }
}
