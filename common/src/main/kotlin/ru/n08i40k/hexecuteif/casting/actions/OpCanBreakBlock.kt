package ru.n08i40k.hexecuteif.casting.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

object OpCanBreakBlock : ConstMediaAction {
    override val argc = 1

    private fun canBreak(env: CastingEnvironment, pos: BlockPos): Boolean {
        if (!env.isVecInRange(Vec3.atCenterOf(pos)))
            return false

        if (!env.canEditBlockAt(pos))
            return false;

        val blockState = env.world.getBlockState(pos)

        if (!IXplatAbstractions.INSTANCE.isBreakingAllowed(env.world, pos, blockState, env.caster))
            return false

        if (blockState.isAir)
            return false

        if (blockState.getDestroySpeed(env.world, pos) < 0f)
            return false

        val tier = HexConfig.server().opBreakHarvestLevel()

        return IXplatAbstractions.INSTANCE.isCorrectTierForDrops(tier, blockState)
    }

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): List<Iota> {
        val pos = args.getBlockPos(0, argc)

        return canBreak(env, pos).asActionResult
    }
}