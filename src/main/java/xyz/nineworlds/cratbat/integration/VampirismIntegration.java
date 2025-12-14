package xyz.nineworlds.cratbat.integration;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;

public class VampirismIntegration {
    private static final boolean VAMPIRISM_LOADED = ModList.get().isLoaded("vampirism");
    private static final ResourceLocation BAT_ACTION = ResourceLocation.fromNamespaceAndPath("vampirism", "bat");

    public static boolean isPlayerInBatForm(Player player) {
        if (!VAMPIRISM_LOADED) {
            return false;
        }

        try {
            // Try to use Vampirism API to check bat form
            return VampirismHelper.isInBatForm(player);
        } catch (Exception e) {
            // If Vampirism API fails, return false
            return false;
        }
    }

    private static class VampirismHelper {
        public static boolean isInBatForm(Player player) {
            try {
                final LazyOptional<IVampirePlayer> vampirePlayerOpt = VampirismAPI.getVampirePlayer(player);
                if (vampirePlayerOpt.isPresent()) {
                    IVampirePlayer vampirePlayer = vampirePlayerOpt.resolve().orElse(null);
                    if (vampirePlayer != null) {
                        final IActionHandler handler = vampirePlayer.getActionHandler();
                        return handler.isActionActive(BAT_ACTION);
                    }
                }
            } catch (Exception e) {
                return false;
            }
            return false;
        }
    }
}