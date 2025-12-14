package xyz.nineworlds.cratbat.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.nineworlds.cratbat.CratBatConfig;
import xyz.nineworlds.cratbat.CratBatMod;

public class CrankCrankDollRecipeCategory implements IRecipeCategory<CrankCrankDollRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(CratBatMod.MODID, "crankcrank_doll_usage");
    public static final RecipeType<CrankCrankDollRecipe> RECIPE_TYPE = RecipeType.create(CratBatMod.MODID, "crankcrank_doll_usage", CrankCrankDollRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public CrankCrankDollRecipeCategory(IGuiHelper guiHelper) {
        // Create a simple background (width, height) - increased height for text
        this.background = guiHelper.createBlankDrawable(160, 70);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(CratBatMod.CRANKCRANK_DOLL.get()));
    }

    @Override
    public RecipeType<CrankCrankDollRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("CrankCrank Doll Usage");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrankCrankDollRecipe recipe, IFocusGroup focuses) {
        // Input: CrankCrank Doll
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 22)
            .addItemStack(recipe.getDoll());

        // Player model will be drawn manually in draw() method - no slot needed

        // Output: Crank Skull
        builder.addSlot(RecipeIngredientRole.OUTPUT, 119, 22)
            .addItemStack(recipe.getResult());
    }

    @Override
    public void draw(CrankCrankDollRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();

        // Try to find the target player by name
        Player targetPlayer = null;
        if (minecraft.level != null) {
            String targetName = CratBatConfig.getTargetPlayerName();
            for (Player player : minecraft.level.players()) {
                if (player.getName().getString().equals(targetName)) {
                    targetPlayer = player;
                    break;
                }
            }
        }

        // Fall back to current player if target not found
        if (targetPlayer == null) {
            targetPlayer = minecraft.player;
        }

        // Draw player model in the middle
        if (targetPlayer != null) {
            int playerX = 60; // Center position for player
            int playerY = 43; // Y position
            int size = 30; // Size of the player model

            InventoryScreen.renderEntityInInventoryFollowsMouse(
                guiGraphics,
                playerX,
                playerY,
                size,
                (float) playerX - (float) mouseX,
                (float) playerY - 30 - (float) mouseY,
                targetPlayer
            );
        }

        // Get the Minecraft font renderer
        Font font = minecraft.font;

        // Draw instruction text with target player name
        String instruction = "Right-click on " + CratBatConfig.getTargetPlayerName();
        int textWidth = font.width(instruction);
        int x = (background.getWidth() - textWidth) / 2; // Center the text
        int y = 52; // Position below the player model

        guiGraphics.drawString(font, instruction, x, y, 0xFF404040, false); // Dark gray color, no shadow
    }
}