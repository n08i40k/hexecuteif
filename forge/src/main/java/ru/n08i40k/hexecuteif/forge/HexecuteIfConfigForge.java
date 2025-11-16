package ru.n08i40k.hexecuteif.forge;

import ru.n08i40k.hexecuteif.api.config.HexecuteIfConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class HexecuteIfConfigForge {

    public static void init() {
        Pair<Common, ForgeConfigSpec> config = (new ForgeConfigSpec.Builder()).configure(Common::new);
        Pair<Client, ForgeConfigSpec> clientConfig = (new ForgeConfigSpec.Builder()).configure(Client::new);
        Pair<Server, ForgeConfigSpec> serverConfig = (new ForgeConfigSpec.Builder()).configure(Server::new);
        HexecuteIfConfig.setCommon(config.getLeft());
        HexecuteIfConfig.setClient(clientConfig.getLeft());
        HexecuteIfConfig.setServer(serverConfig.getLeft());
        ModLoadingContext mlc = ModLoadingContext.get();
        mlc.registerConfig(ModConfig.Type.COMMON, config.getRight());
        mlc.registerConfig(ModConfig.Type.CLIENT, clientConfig.getRight());
        mlc.registerConfig(ModConfig.Type.SERVER, serverConfig.getRight());
    }

    public static class Common implements HexecuteIfConfig.CommonConfigAccess {
        private static ForgeConfigSpec.DoubleValue multiplyContainerAccess;
        private static ForgeConfigSpec.DoubleValue multiplyPlayerAccess;

        private static ForgeConfigSpec.BooleanValue canAccessContainer;
        private static ForgeConfigSpec.BooleanValue canAccessPlayer;

        private static ForgeConfigSpec.BooleanValue canModifyContainer;
        private static ForgeConfigSpec.BooleanValue canModifyPlayer;


        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("Media Multipliers");
            multiplyContainerAccess = builder.comment("How much will the amount of media required to use spells that require access to the container's inventory be multiplied?")
                    .defineInRange("multiplyContainerAccess", DEFAULT_MULTIPLY_CONTAINER_ACCESS, 0.0, 100.0);
            multiplyPlayerAccess = builder.comment("How much will the amount of media required to use spells that require access to the player’s inventory be multiplied?")
                    .defineInRange("multiplyPlayerAccess", DEFAULT_MULTIPLY_PLAYER_ACCESS, 0.0, 100.0);
            builder.pop();

            builder.push("Inventory Access");
            canAccessContainer = builder.comment("Can a spell that requires access to inventory work with a container?")
                    .define("canAccessContainer", DEFAULT_CAN_ACCESS_CONTAINER);
            canAccessPlayer = builder.comment("Can a spell that requires access to inventory work with a player?")
                    .define("canAccessPlayer", DEFAULT_CAN_ACCESS_PLAYER);
            builder.pop();

            builder.push("Inventory Modify");
            canModifyContainer = builder.comment("Can a spell that changes inventory items work with a container?")
                            .define("canModifyContainer", DEFAULT_CAN_MODIFY_CONTAINER);
            canModifyPlayer = builder.comment("Can a spell that changes inventory items work with a player?")
                            .define("canModifyPlayer", DEFAULT_CAN_MODIFY_PLAYER);
            builder.pop();
        }

        @Override
        public double multiplyContainerAccess() {
            return multiplyContainerAccess.get();
        }

        @Override
        public double multiplyPlayerAccess() {
            return multiplyPlayerAccess.get();
        }

        @Override
        public boolean canAccessContainer() {
            return canAccessContainer.get();
        }

        @Override
        public boolean canAccessPlayer() {
            return canAccessPlayer.get();
        }

        @Override
        public boolean canModifyContainer() {
            return canModifyContainer.get();
        }

        @Override
        public boolean canModifyPlayer() {
            return canModifyPlayer.get();
        }
    }

    public static class Client implements HexecuteIfConfig.ClientConfigAccess {
        public Client(ForgeConfigSpec.Builder builder) {

        }
    }

    public static class Server implements HexecuteIfConfig.ServerConfigAccess {
        public Server(ForgeConfigSpec.Builder builder) {
        }
    }
}
