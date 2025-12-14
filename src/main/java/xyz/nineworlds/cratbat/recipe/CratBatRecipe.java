package xyz.nineworlds.cratbat.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import xyz.nineworlds.cratbat.CratBatMod;
import xyz.nineworlds.cratbat.item.CrankSkullItem;

public class CratBatRecipe extends CustomRecipe {
    public CratBatRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean foundCrankSkull = false;
        int stickCount = 0;
        int batWingCount = 0;
        int emptySlots = 0;

        // Check the 3x3 pattern:
        // [Bat Wing] [Crank Skull] [Bat Wing]
        //     [ ]        [Stick]       [ ]
        //     [ ]        [Stick]       [ ]

        // Top row
        ItemStack topLeft = container.getItem(0);
        ItemStack topCenter = container.getItem(1);
        ItemStack topRight = container.getItem(2);

        // Middle row
        ItemStack middleLeft = container.getItem(3);
        ItemStack middleCenter = container.getItem(4);
        ItemStack middleRight = container.getItem(5);

        // Bottom row
        ItemStack bottomLeft = container.getItem(6);
        ItemStack bottomCenter = container.getItem(7);
        ItemStack bottomRight = container.getItem(8);

        // Check pattern
        if (topLeft.is(CratBatMod.BAT_WING.get()) && topRight.is(CratBatMod.BAT_WING.get()) &&
            CrankSkullItem.isCrankSkull(topCenter) &&
            middleCenter.is(Items.STICK) && bottomCenter.is(Items.STICK) &&
            middleLeft.isEmpty() && middleRight.isEmpty() &&
            bottomLeft.isEmpty() && bottomRight.isEmpty()) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return new ItemStack(CratBatMod.CRATBAT.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CratBatRecipeSerializer.INSTANCE;
    }
}