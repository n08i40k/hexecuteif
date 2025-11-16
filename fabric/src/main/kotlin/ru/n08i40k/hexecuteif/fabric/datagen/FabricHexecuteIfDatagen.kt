package ru.n08i40k.hexecuteif.fabric.datagen

import ru.n08i40k.hexecuteif.datagen.HexecuteIfActionTags
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object FabricHexecuteIfDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        val pack = gen.createPack()

        pack.addProvider(::HexecuteIfActionTags)
    }
}
