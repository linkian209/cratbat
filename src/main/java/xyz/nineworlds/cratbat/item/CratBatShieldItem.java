package xyz.nineworlds.cratbat.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class CratBatShieldItem extends Item implements ICurioItem {
    public CratBatShieldItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Shields you from the CratBat's power").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Reward from Vampirism task completion").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // No special tick behavior needed
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() != null) {
            slotContext.entity().sendSystemMessage(
                Component.literal("You feel protected from the CratBat's power...").withStyle(ChatFormatting.GREEN)
            );
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() != null) {
            slotContext.entity().sendSystemMessage(
                Component.literal("The protection fades away...").withStyle(ChatFormatting.RED)
            );
        }
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}