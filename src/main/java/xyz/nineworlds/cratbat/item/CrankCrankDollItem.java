package xyz.nineworlds.cratbat.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import xyz.nineworlds.cratbat.CratBatConfig;
import xyz.nineworlds.cratbat.entity.TestCratEntity;

public class CrankCrankDollItem extends Item {
    public CrankCrankDollItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof Player targetPlayer) {
            String targetName = targetPlayer.getName().getString();

            // Check if the target player matches the configured name
            if (CratBatConfig.getTargetPlayerName().equals(targetName)) {
                if (!player.level().isClientSide) {
                    // Create the Crank Skull and add to player inventory
                    ItemStack crankSkull = CrankSkullItem.createCrankSkull();
                    if (!player.getInventory().add(crankSkull)) {
                        // If inventory is full, drop it
                        player.drop(crankSkull, false);
                    }

                    // Consume the CrankCrank Doll
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    // Send ominous message only to the user
                    player.sendSystemMessage(Component.literal("The doll's eyes glow with dark energy... A skull materializes."));
                }
                return InteractionResult.sidedSuccess(player.level().isClientSide);
            } else {
                // Wrong target player
                if (!player.level().isClientSide) {
                    player.sendSystemMessage(Component.literal("The doll remains lifeless when used on " + targetName + "."));
                }
                return InteractionResult.FAIL;
            }
        }

        // Also work on TestCrat entities for testing purposes
        if (target instanceof TestCratEntity) {
            if (!player.level().isClientSide) {
                // Create the Crank Skull and add to player inventory
                ItemStack crankSkull = CrankSkullItem.createCrankSkull();
                if (!player.getInventory().add(crankSkull)) {
                    // If inventory is full, drop it
                    player.drop(crankSkull, false);
                }

                // Consume the CrankCrank Doll
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                // Send ominous message only to the user
                player.sendSystemMessage(Component.literal("The doll's eyes glow with dark energy... A skull materializes from the TestCrat."));
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // This method is for right-clicking on blocks, not what we want
        return InteractionResult.PASS;
    }
}