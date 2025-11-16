package ru.n08i40k.hexecuteif.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import ru.n08i40k.hexecuteif.HexecuteIf;

// scuffed workaround for https://github.com/architectury/architectury-loom/issues/189
@Mixin({
    net.minecraft.data.Main.class,
    net.minecraft.server.Main.class,
})
public abstract class MixinDatagenMain {
    @WrapMethod(method = "main", remap = false)
    private static void hexecuteif$systemExitAfterDatagenFinishes(String[] strings, Operation<Void> original) {
        try {
            original.call((Object) strings);
        } catch (Throwable throwable) {
            HexecuteIf.LOGGER.error("Datagen failed!", throwable);
            System.exit(1);
        }
        HexecuteIf.LOGGER.info("Datagen succeeded, terminating.");
        System.exit(0);
    }
}
