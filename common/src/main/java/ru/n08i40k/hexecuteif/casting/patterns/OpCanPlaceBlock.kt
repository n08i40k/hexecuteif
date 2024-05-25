package ru.n08i40k.hexecuteif.casting.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.asActionResult
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getBlockPos
import at.petrak.hexcasting.api.spell.getBool
import at.petrak.hexcasting.api.spell.iota.Iota
import net.minecraft.core.BlockPos
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.Vec3

object OpCanPlaceBlock : ConstMediaAction {
    override val argc: Int
        get() = 2

    private fun canPlace(ctx: CastingContext, pos: BlockPos, allowLiquids: Boolean): Boolean {
        if (!ctx.isVecInRange(Vec3.atCenterOf(pos)))
            return false

        if (!ctx.canEditBlockAt(pos))
            return false;

        val blockState = ctx.world.getBlockState(pos)

        return blockState.isAir || (allowLiquids && !blockState.fluidState.`is`(Fluids.EMPTY))
    }

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val pos = args.getBlockPos(0, argc)
        val checkLiquids = args.getBool(1, argc)

        return canPlace(ctx, pos, checkLiquids).asActionResult
    }
}