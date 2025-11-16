@file:JvmName("HexecuteIfAbstractions")

package ru.n08i40k.hexecuteif

import dev.architectury.injectables.annotations.ExpectPlatform
import ru.n08i40k.hexecuteif.registry.HexecuteIfRegistrar

fun initRegistries(vararg registries: HexecuteIfRegistrar<*>) {
    for (registry in registries) {
        initRegistry(registry)
    }
}

@ExpectPlatform
fun <T : Any> initRegistry(registrar: HexecuteIfRegistrar<T>) {
    throw AssertionError()
}
