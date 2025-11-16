@file:JvmName("HexecuteIfAbstractionsImpl")

package ru.n08i40k.hexecuteif.fabric

import ru.n08i40k.hexecuteif.registry.HexecuteIfRegistrar
import net.minecraft.core.Registry

fun <T : Any> initRegistry(registrar: HexecuteIfRegistrar<T>) {
    val registry = registrar.registry
    registrar.init { id, value -> Registry.register(registry, id, value) }
}
