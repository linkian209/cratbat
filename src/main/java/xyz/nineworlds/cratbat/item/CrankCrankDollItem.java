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
                    // Create and drop the Crank Skull
                    ItemStack crankSkull = CrankSkullItem.createCrankSkull();
                    targetPlayer.drop(crankSkull, false);

                    // Consume the CrankCrank Doll
                    stack.shrink(1);

                    // Send ominous message only to the user
                    player.sendSystemMessage(Component.literal("The doll's eyes glow with dark energy... A skull materializes."));
                }
                return InteractionResult.SUCCESS;
            } else {
                // Wrong target player
                if (!player.level().isClientSide) {
                    player.sendSystemMessage(Component.literal("The doll remains lifeless when used on " + targetName + "."));
                }
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // This method is for right-clicking on blocks, not what we want
        return InteractionResult.PASS;
    }
}