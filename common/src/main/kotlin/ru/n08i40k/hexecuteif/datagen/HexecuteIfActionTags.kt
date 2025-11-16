package ru.n08i40k.hexecuteif.datagen

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.common.lib.HexRegistries
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.TagsProvider
import ru.n08i40k.hexecuteif.registry.HexecuteIfRegistrar
import java.util.concurrent.CompletableFuture

class HexecuteIfActionTags(
    output: PackOutput,
    provider: CompletableFuture<HolderLookup.Provider>,
) : TagsProvider<ActionRegistryEntry>(output, HexRegistries.ACTION, provider) {
    override fun addTags(provider: HolderLookup.Provider) {
        for (entry in arrayOf<HexecuteIfRegistrar<ActionRegistryEntry>.Entry<ActionRegistryEntry>>()) {
            tag(HexTags.Actions.CAN_START_ENLIGHTEN).add(entry.key)
            tag(HexTags.Actions.PER_WORLD_PATTERN).add(entry.key)
            tag(HexTags.Actions.REQUIRES_ENLIGHTENMENT).add(entry.key)
        }
    }
}
