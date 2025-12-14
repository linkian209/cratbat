package xyz.nineworlds.cratbat.event;

import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.nineworlds.cratbat.CratBatMod;
import xyz.nineworlds.cratbat.item.CrankSkullItem;

@Mod.EventBusSubscriber(modid = CratBatMod.MODID)
public class BatDropHandler {

    @SubscribeEvent
    public static void onBatDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Bat bat && event.getSource().getEntity() instanceof Player player) {
            // Check if player is holding a Crank Skull in either hand
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            boolean holdingCrankSkull = CrankSkullItem.isCrankSkull(mainHand) || CrankSkullItem.isCrankSkull(offHand);

            if (holdingCrankSkull) {
                // Drop 1-2 bat wings when a bat is killed while holding a Crank Skull
                int dropCount = bat.getRandom().nextInt(2) + 1; // 1 or 2 bat wings

                for (int i = 0; i < dropCount; i++) {
                    ItemStack batWing = new ItemStack(CratBatMod.BAT_WING.get());
                    ItemEntity itemEntity = new ItemEntity(
                        bat.level(),
                        bat.getX(),
                        bat.getY(),
                        bat.getZ(),
                        batWing
                    );
                    bat.level().addFreshEntity(itemEntity);
                }
            }
        }
    }
}