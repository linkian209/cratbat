package xyz.nineworlds.cratbat.event;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xyz.nineworlds.cratbat.network.CratBatNetwork;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import xyz.nineworlds.cratbat.CratBatConfig;
import xyz.nineworlds.cratbat.CratBatMod;
import xyz.nineworlds.cratbat.integration.VampirismIntegration;
import xyz.nineworlds.cratbat.integration.VampirismTaskIntegration;

public class CratBatEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player attacker) {
            // Check if the attacker is using a CratBat
            if (attacker.getMainHandItem().getItem() == CratBatMod.CRATBAT.get()) {
                if (event.getEntity() instanceof Player victim) {
                    String victimName = victim.getName().getString();

                    // Check if victim is the configured target player
                    if (CratBatConfig.getTargetPlayerName().equals(victimName)) {
                        // Check if victim has CratBat Shield equipped via Curios
                        boolean hasProtection = CuriosApi.getCuriosInventory(victim)
                            .map(curios -> curios.findFirstCurio(CratBatMod.CRATBAT_SHIELD.get()).isPresent())
                            .orElse(false);

                        if (hasProtection) {
                            // Cancel damage if CratBat Shield is equipped
                            event.setCanceled(true);
                            LOGGER.info("CratBat damage blocked by CratBat Shield for {}", victimName);
                            victim.sendSystemMessage(Component.literal("Your CratBat Shield absorbs the CratBat's power!").withStyle(ChatFormatting.GOLD));
                        } else {
                            // Check if victim is in bat form
                            if (VampirismIntegration.isPlayerInBatForm(victim)) {
                                // Deal massive damage (99,999,999,999,999)
                                event.setAmount(99999999999999f);
                                LOGGER.info("CratBat special damage triggered: {} swatted {} in bat form",
                                    attacker.getName().getString(), victimName);
                            } else {
                                LOGGER.info("CratBat hit target player {} but they are not in bat form", victimName);
                            }
                        }
                    } else {
                        // Not the target player - normal damage (but we could add immunity here)
                        LOGGER.info("CratBat hit non-target player {}", victimName);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player attacker) {
            // Check if the attacker is using a CratBat
            if (attacker.getMainHandItem().getItem() == CratBatMod.CRATBAT.get()) {
                if (event.getEntity() instanceof Player victim) {
                    String victimName = victim.getName().getString();
                    String attackerName = attacker.getName().getString();

                    // Check if victim is the configured target player and was in bat form
                    if (CratBatConfig.getTargetPlayerName().equals(victimName) &&
                        VampirismIntegration.isPlayerInBatForm(victim)) {

                        // Send custom death message to all players in the world
                        Component deathMessage = Component.literal("The Crat (" + victimName + ") was swatted by " + attackerName);

                        // Broadcast to all players on the server
                        victim.getServer().getPlayerList().getPlayers().forEach(player -> {
                            player.sendSystemMessage(deathMessage);
                        });

                        // Complete Vampirism task and award CratBat Shield
                        VampirismTaskIntegration.completeTaskForPlayer(victim);

                        LOGGER.info("Custom death message sent: The Crat ({}) was swatted by {}", victimName, attackerName);
                    }
                }
            }
        }
    }

    /**
     * Syncs server configuration to the client when a player logs in.
     * Only runs on the server side.
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            LOGGER.debug("Player {} logged in, sending config sync", serverPlayer.getName().getString());
            CratBatNetwork.sendToPlayer(serverPlayer);
        }
    }

    /**
     * Clears server configuration override when a player logs out.
     * Only runs on the client side to revert to local config.
     */
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        // Only clear server config on client side when disconnecting
        if (FMLEnvironment.dist == Dist.CLIENT) {
            LOGGER.debug("Player logged out, clearing server config");
            CratBatConfig.clearServerConfig();
        }
    }
}