package xyz.nineworlds.cratbat.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CratBatRecipeSerializer implements RecipeSerializer<CratBatRecipe> {
    public static final CratBatRecipeSerializer INSTANCE = new CratBatRecipeSerializer();

    @Override
    public CratBatRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        CraftingBookCategory category = CraftingBookCategory.MISC;
        if (json.has("category")) {
            category = CraftingBookCategory.CODEC.byName(json.get("category").getAsString(), CraftingBookCategory.MISC);
        }
        return new CratBatRecipe(recipeId, category);
    }

    @Override
    public CratBatRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
        return new CratBatRecipe(recipeId, category);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, CratBatRecipe recipe) {
        buffer.writeEnum(recipe.category());
    }
}