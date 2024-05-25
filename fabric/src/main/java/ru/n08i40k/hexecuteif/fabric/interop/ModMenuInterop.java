package ru.n08i40k.hexecuteif.fabric.interop;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.n08i40k.hexecuteif.fabric.HexecuteIfConfigFabric;

@Environment(EnvType.CLIENT)
public class ModMenuInterop implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(HexecuteIfConfigFabric.class, parent).get();
    }
}
