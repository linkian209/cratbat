package xyz.nineworlds.cratbat.event;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import xyz.nineworlds.cratbat.CratBatConfig;
import xyz.nineworlds.cratbat.CratBatMod;
import xyz.nineworlds.cratbat.core.ModDamageSources;
import xyz.nineworlds.cratbat.core.ModDamageTypes;
import xyz.nineworlds.cratbat.entity.TestCratEntity;
import xyz.nineworlds.cratbat.integration.VampirismIntegration;
import xyz.nineworlds.cratbat.integration.VampirismTaskIntegration;
import xyz.nineworlds.cratbat.network.CratBatNetwork;

public class CratBatEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Float CRAT_BAT_DAMAGE = 999_999_999_999f;

    /**
     * Spawns a visual-only lightning bolt at the entity's location for dramatic effect.
     */
    private void spawnLightningEffect(LivingEntity entity) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.setPos(entity.getX(), entity.getY(), entity.getZ());
                lightning.setVisualOnly(true);  // No damage, no fire - just the effect
                serverLevel.addFreshEntity(lightning);
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        // Skip if this is already our custom damage (prevents infinite recursion)
        if (event.getSource().is(ModDamageTypes.CRAT_BAT_STRIKE)) {
            return;
        }

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
                                // Cancel the original "player" damage event
                                event.setCanceled(true);

                                // Apply custom damage source that bypasses Vampirism DBNO
                                // The custom "crat_bat_strike" type is not in Vampirism's immortalFromDamageSources list
                                DamageSource source = ModDamageSources.from(victim.level()).cratBatStrike(attacker);
                                victim.hurt(source, CRAT_BAT_DAMAGE);

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

                // Check if victim is a TestCrat entity in bat form
                if (event.getEntity() instanceof TestCratEntity testCrat) {
                    if (testCrat.isInBatForm()) {
                        // Cancel the original damage event
                        event.setCanceled(true);

                        // Apply custom damage source
                        DamageSource source = ModDamageSources.from(testCrat.level()).cratBatStrike(attacker);
                        testCrat.hurt(source, CRAT_BAT_DAMAGE);

                        LOGGER.info("CratBat special damage triggered: {} swatted TestCrat in bat form",
                            attacker.getName().getString());
                    } else {
                        LOGGER.info("CratBat hit TestCrat but they are not in bat form");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        // Check if this death was caused by our custom CratBat damage source
        // If so, we already validated all conditions (target player, bat form, etc.) in onLivingHurt
        if (!event.getSource().is(ModDamageTypes.CRAT_BAT_STRIKE)) {
            return;
        }

        // Get the attacker from the damage source
        if (!(event.getSource().getEntity() instanceof Player attacker)) {
            return;
        }

        String attackerName = attacker.getName().getString();

        // Handle player victim
        if (event.getEntity() instanceof Player victim) {
            String victimName = victim.getName().getString();

            // Spawn lightning effect for dramatic flair
            spawnLightningEffect(victim);

            // Send custom death message to all players
            Component deathMessage = Component.literal("The Crat (" + victimName + ") was swatted by " + attackerName);

            if (victim.getServer() != null) {
                victim.getServer().getPlayerList().getPlayers().forEach(
                    player -> player.sendSystemMessage(deathMessage));
            }

            // Award CratBat Shield
            VampirismTaskIntegration.completeTaskForPlayer(victim);

            LOGGER.info("CratBat kill: The Crat ({}) was swatted by {}", victimName, attackerName);
        }

        // Handle TestCrat victim
        if (event.getEntity() instanceof TestCratEntity testCrat) {
            // Spawn lightning effect for dramatic flair
            spawnLightningEffect(testCrat);

            Component deathMessage = Component.literal("A TestCrat was swatted by " + attackerName);

            if (testCrat.getServer() != null) {
                testCrat.getServer().getPlayerList().getPlayers().forEach(
                    player -> player.sendSystemMessage(deathMessage));
            }

            LOGGER.info("CratBat kill: A TestCrat was swatted by {}", attackerName);
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