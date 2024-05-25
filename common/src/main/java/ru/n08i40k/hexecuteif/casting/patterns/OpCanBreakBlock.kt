package ru.n08i40k.hexecuteif.casting.patterns

import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

object OpCanBreakBlock : ConstMediaAction {
    override val argc: Int
        get() = 1

    private fun canBreak(ctx: CastingContext, pos: BlockPos): Boolean {
        if (!ctx.isVecInRange(Vec3.atCenterOf(pos)))
            return false

        if (!ctx.canEditBlockAt(pos))
            return false;

        val blockState = ctx.world.getBlockState(pos)

        if (!IXplatAbstractions.INSTANCE.isBreakingAllowed(ctx.world, pos, blockState, ctx.caster))
            return false

        if (blockState.isAir)
            return false

        if (blockState.getDestroySpeed(ctx.world, pos) < 0f)
            return false

        val tier = HexConfig.server().opBreakHarvestLevel()

        return IXplatAbstractions.INSTANCE.isCorrectTierForDrops(tier, blockState)
    }

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val pos = args.getBlockPos(0, argc)

        return canBreak(ctx, pos).asActionResult
    }
}