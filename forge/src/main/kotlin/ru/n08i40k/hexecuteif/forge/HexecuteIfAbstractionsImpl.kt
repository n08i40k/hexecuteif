@file:JvmName("HexecuteIfAbstractionsImpl")

package ru.n08i40k.hexecuteif.forge

import ru.n08i40k.hexecuteif.registry.HexecuteIfRegistrar
import net.minecraftforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

fun <T : Any> initRegistry(registrar: HexecuteIfRegistrar<T>) {
    MOD_BUS.addListener { event: RegisterEvent ->
        event.register(registrar.registryKey) { helper ->
            registrar.init(helper::register)
        }
    }
}
