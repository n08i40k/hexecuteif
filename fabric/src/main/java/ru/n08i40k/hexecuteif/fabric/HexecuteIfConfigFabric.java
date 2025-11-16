package ru.n08i40k.hexecuteif.fabric;

import dev.architectury.platform.Platform;
import ru.n08i40k.hexecuteif.HexecuteIf;
import ru.n08i40k.hexecuteif.api.config.HexecuteIfConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.EnvType;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@Config(name = HexecuteIf.MOD_ID)
public class HexecuteIfConfigFabric extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.TransitiveObject
    public final Common common = new Common();
    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public final Client client = new Client();
    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public final Server server = new Server();

    public static void init() {
        AutoConfig.register(HexecuteIfConfigFabric.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        var instance = AutoConfig.getConfigHolder(HexecuteIfConfigFabric.class).getConfig();

        HexecuteIfConfig.setCommon(instance.common);

        if (Platform.getEnv().equals(EnvType.CLIENT)) {
            HexecuteIfConfig.setClient(instance.client);
        }

        // Needed for logical server in singleplayer, do not access server configs from client code
        HexecuteIfConfig.setServer(instance.server);
    }


    @Config(name = "common")
    private static class Common implements ConfigData, HexecuteIfConfig.CommonConfigAccess {
        @ConfigEntry.Gui.Tooltip
        private double multiplyContainerAccess = DEFAULT_MULTIPLY_CONTAINER_ACCESS;
        @ConfigEntry.Gui.Tooltip
        private double multiplyPlayerAccess = DEFAULT_MULTIPLY_PLAYER_ACCESS;

        @ConfigEntry.Gui.Tooltip
        private boolean canAccessContainer = DEFAULT_CAN_ACCESS_CONTAINER;
        @ConfigEntry.Gui.Tooltip
        private boolean canAccessPlayer = DEFAULT_CAN_ACCESS_PLAYER;

        @ConfigEntry.Gui.Tooltip
        private boolean canModifyContainer = DEFAULT_CAN_MODIFY_CONTAINER;
        @ConfigEntry.Gui.Tooltip
        private boolean canModifyPlayer = DEFAULT_CAN_MODIFY_PLAYER;

        @Override
        public void validatePostLoad() throws ValidationException {
            this.multiplyContainerAccess = Math.max(0.0, Math.min(100.0, this.multiplyContainerAccess));
            this.multiplyPlayerAccess = Math.max(0.0, Math.min(100.0, this.multiplyPlayerAccess));
        }

        @Override
        public double multiplyContainerAccess() {
            return multiplyContainerAccess;
        }

        @Override
        public double multiplyPlayerAccess() {
            return multiplyPlayerAccess;
        }

        @Override
        public boolean canAccessContainer() {
            return canAccessContainer;
        }

        @Override
        public boolean canAccessPlayer() {
            return canAccessPlayer;
        }

        @Override
        public boolean canModifyContainer() {
            return canModifyContainer;
        }

        @Override
        public boolean canModifyPlayer() {
            return canModifyPlayer;
        }
    }

    @Config(name = "client")
    private static class Client implements ConfigData, HexecuteIfConfig.ClientConfigAccess {
    }


    @Config(name = "server")
    private static class Server implements ConfigData, HexecuteIfConfig.ServerConfigAccess {
    }
}