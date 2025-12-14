package xyz.nineworlds.cratbat.integration;

import com.mojang.logging.LogUtils;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;
import xyz.nineworlds.cratbat.CratBatMod;

public class VampirismTaskIntegration {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean VAMPIRISM_LOADED = ModList.get().isLoaded("vampirism");
    private static final ResourceLocation CRATBAT_TASK_ID = ResourceLocation.fromNamespaceAndPath("cratbat", "be_swatted_no_more");

    public static void completeTaskForPlayer(Player player) {
        if (!VAMPIRISM_LOADED) {
            return;
        }

        try {
            VampirismTaskHelper.completeCratBatTask(player);
        } catch (Exception e) {
            LOGGER.error("Failed to complete Vampirism task for player {}: {}", player.getName().getString(), e.getMessage());
        }
    }

    private static class VampirismTaskHelper {
        public static void completeCratBatTask(Player player) {
            LazyOptional<IVampirePlayer> vampirePlayerOpt = VampirismAPI.getVampirePlayer(player);
            if (vampirePlayerOpt.isPresent()) {
                IVampirePlayer vampirePlayer = vampirePlayerOpt.resolve().orElse(null);
                if (vampirePlayer != null) {
                    // Award CratBat Shield directly
                ItemStack cratbatShield = new ItemStack(CratBatMod.CRATBAT_SHIELD.get());

                    if (!player.getInventory().add(cratbatShield)) {
                        // Drop it if inventory is full
                        player.drop(cratbatShield, false);
                    }

                    player.sendSystemMessage(Component.literal("Task 'Be swatted no more' completed! You received a CratBat Shield."));
                    LOGGER.info("Awarded CratBat Shield to {} for completing CratBat task", player.getName().getString());
                }
            }
        }
    }
}