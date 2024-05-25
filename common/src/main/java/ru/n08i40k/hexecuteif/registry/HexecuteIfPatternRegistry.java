package ru.n08i40k.hexecuteif.registry;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import kotlin.Triple;
import ru.n08i40k.hexecuteif.casting.patterns.OpCanBreakBlock;
import ru.n08i40k.hexecuteif.casting.patterns.OpCanPlaceBlock;
import ru.n08i40k.hexecuteif.casting.patterns.eval.OpEvalIf;
import net.minecraft.resources.ResourceLocation;
import ru.n08i40k.hexecuteif.casting.patterns.eval.OpHaltIf;
import ru.n08i40k.hexecuteif.casting.patterns.spell.*;

import java.util.ArrayList;
import java.util.List;

import static ru.n08i40k.hexecuteif.HexecuteIf.id;

public class HexecuteIfPatternRegistry {
    public static List<Triple<HexPattern, ResourceLocation, Action>> PATTERNS = new ArrayList<>();
    public static List<Triple<HexPattern, ResourceLocation, Action>> PER_WORLD_PATTERNS = new ArrayList<>();

    public static HexPattern EVAL_IF = register(HexPattern.fromAngles("deaqqq", HexDir.SOUTH_EAST), "eval_if", OpEvalIf.INSTANCE);
    public static HexPattern HALT_IF = register(HexPattern.fromAngles("aqdeee", HexDir.SOUTH_WEST), "halt_if", OpHaltIf.INSTANCE);

    public static HexPattern CAN_BREAK_BLOCK = register(HexPattern.fromAngles("qaqqqqqwa", HexDir.EAST), "can_break_block", OpCanBreakBlock.INSTANCE);
    public static HexPattern CAN_PLACE_BLOCK = register(HexPattern.fromAngles("eeeeedeed", HexDir.SOUTH_EAST), "can_place_block", OpCanPlaceBlock.INSTANCE);

    public static HexPattern INV_SLOT_COUNT = register(HexPattern.fromAngles("qwawqwaqw", HexDir.SOUTH_EAST), "inv_slot_count", OpInvSlotCount.INSTANCE);
    public static HexPattern INV_OCCUPIED_SLOTS = register(HexPattern.fromAngles("dwewdwed", HexDir.NORTH_WEST), "inv_occupied_slots", OpInvOccupiedSlots.INSTANCE);
    public static HexPattern INV_SLOT_ITEM_COUNT = register(HexPattern.fromAngles("dwewdweedq", HexDir.NORTH_WEST), "inv_slot_item_count", OpInvSlotItemCount.INSTANCE);

    public static HexPattern INV_DROP_ITEM = register(HexPattern.fromAngles("dwewdwedwwadeeed", HexDir.NORTH_WEST), "inv_drop_item", OpInvDropItem.INSTANCE);
    public static HexPattern INV_TRANSFER_ITEM = register(HexPattern.fromAngles("dwewdwedwwadeeedewadeeed", HexDir.NORTH_WEST), "inv_transfer_item", OpInvTransferItem.INSTANCE);

    public static void init() {
        try {
            for (Triple<HexPattern, ResourceLocation, Action> patternTriple : PATTERNS) {
                PatternRegistry.mapPattern(patternTriple.getFirst(), patternTriple.getSecond(), patternTriple.getThird());
            }
            for (Triple<HexPattern, ResourceLocation, Action> patternTriple : PER_WORLD_PATTERNS) {
                PatternRegistry.mapPattern(patternTriple.getFirst(), patternTriple.getSecond(), patternTriple.getThird(), true);
            }
        } catch (PatternRegistry.RegisterPatternException e) {
            e.printStackTrace();
        }
    }

    private static HexPattern register(HexPattern pattern, String name, Action action) {
        Triple<HexPattern, ResourceLocation, Action> triple = new Triple<>(pattern, id(name), action);
        PATTERNS.add(triple);
        return pattern;
    }

    private static HexPattern registerPerWorld(HexPattern pattern, String name, Action action) {
        Triple<HexPattern, ResourceLocation, Action> triple = new Triple<>(pattern, id(name), action);
        PER_WORLD_PATTERNS.add(triple);
        return pattern;
    }
}
