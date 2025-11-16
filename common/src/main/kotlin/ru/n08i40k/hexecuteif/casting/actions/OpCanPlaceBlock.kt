package ru.n08i40k.hexecuteif.casting.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getBool
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.core.BlockPos
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.Vec3

object OpCanPlaceBlock : ConstMediaAction {
    override val argc = 2

    private fun canPlace(env: CastingEnvironment, pos: BlockPos, allowLiquids: Boolean): Boolean {
        if (!env.isVecInRange(Vec3.atCenterOf(pos)))
            return false

        if (!env.canEditBlockAt(pos))
            return false;

        val blockState = env.world.getBlockState(pos)

        return blockState.isAir || (allowLiquids && !blockState.fluidState.`is`(Fluids.EMPTY))
    }

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val pos = args.getBlockPos(0, argc)
        val checkLiquids = args.getBool(1, argc)

        return canPlace(env, pos, checkLiquids).asActionResult
    }
}