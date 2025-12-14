package xyz.nineworlds.cratbat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import xyz.nineworlds.cratbat.CratBatConfig;
import xyz.nineworlds.cratbat.network.CratBatNetwork;
import xyz.nineworlds.cratbat.util.PlayerTextureUtil;

/**
 * Command handler for the CratBat mod.
 * Provides /cratbat setCrat <player> command for administrators.
 */
public class CratBatCommand {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Registers all CratBat commands with the command dispatcher.
     *
     * @param dispatcher The command dispatcher
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("cratbat")
                        .requires(source -> source.hasPermission(2)) // Requires operator level 2
                        .then(Commands.literal("setCrat")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(CratBatCommand::setCratPlayer)))
                        .then(Commands.literal("info")
                                .executes(CratBatCommand::showInfo))
        );

        LOGGER.info("CratBat commands registered");
    }

    /**
     * Executes the setCrat command to set a new target player.
     * Updates config, persists to disk, and broadcasts to all clients.
     *
     * @param context The command context
     * @return 1 on success, 0 on failure
     */
    private static int setCratPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        CommandSourceStack source = context.getSource();

        String playerName = targetPlayer.getName().getString();
        String playerUuid = targetPlayer.getUUID().toString();
        String textureUrl = PlayerTextureUtil.getTextureUrl(targetPlayer.getGameProfile());

        LOGGER.info("Setting CratBat target to {} (UUID: {}, Texture: {})",
                playerName, playerUuid, !textureUrl.isEmpty() ? "found" : "not found");

        // Update the local config (this will persist to disk)
        CratBatConfig.updateLocalConfig(playerName, playerUuid, textureUrl);

        // Broadcast the new config to all connected players
        CratBatNetwork.broadcastConfigToAll();

        // Send success message to command executor
        source.sendSuccess(() -> Component.literal("CratBat target set to " + playerName), true);

        if (textureUrl.isEmpty()) {
            source.sendSystemMessage(Component.literal("Warning: Could not extract texture URL for " + playerName + ". Skull may not display correctly."));
        }

        return 1;
    }

    /**
     * Shows current CratBat configuration information.
     *
     * @param context The command context
     * @return 1 on success
     */
    private static int showInfo(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String targetName = CratBatConfig.getTargetPlayerName();
        String targetUuid = CratBatConfig.getTargetPlayerUUID();
        String targetTexture = CratBatConfig.getTargetPlayerTexture();
        boolean hasServerConfig = CratBatConfig.hasServerConfig();

        source.sendSystemMessage(Component.literal("=== CratBat Configuration ==="));
        source.sendSystemMessage(Component.literal("Target Player: " + targetName));
        source.sendSystemMessage(Component.literal("Target UUID: " + targetUuid));
        source.sendSystemMessage(Component.literal("Has Texture: " + (!targetTexture.isEmpty() ? "Yes" : "No")));
        source.sendSystemMessage(Component.literal("Server Config Active: " + (hasServerConfig ? "Yes" : "No")));

        return 1;
    }
}
