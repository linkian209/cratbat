package xyz.nineworlds.cratbat.integration.jei;

import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.slf4j.Logger;
import xyz.nineworlds.cratbat.CratBatMod;
import xyz.nineworlds.cratbat.item.CrankSkullItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class CratBatJEIPlugin implements IModPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CratBatMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // Register custom recipe category for CrankCrank Doll usage
        registration.addRecipeCategories(new CrankCrankDollRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        LOGGER.info("Registered CrankCrank Doll recipe category");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Create a visual representation of the CratBat recipe for JEI
        // Pattern:
        // [Bat Wing] [Crank Skull] [Bat Wing]
        //     [ ]        [Stick]       [ ]
        //     [ ]        [Stick]       [ ]

        try {
            // Define pattern key mapping
            Map<Character, Ingredient> key = new HashMap<>();
            key.put('B', Ingredient.of(new ItemStack(CratBatMod.BAT_WING.get())));
            key.put('C', Ingredient.of(CrankSkullItem.createCrankSkull()));
            key.put('S', Ingredient.of(Items.STICK));

            // Define the pattern
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
            ingredients.set(0, key.get('B'));  // top-left
            ingredients.set(1, key.get('C'));  // top-center
            ingredients.set(2, key.get('B'));  // top-right
            ingredients.set(4, key.get('S'));  // middle-center
            ingredients.set(7, key.get('S'));  // bottom-center

            ItemStack result = new ItemStack(CratBatMod.CRATBAT.get());

            // Create ShapedRecipe for JEI display
            ShapedRecipe cratBatRecipe = new ShapedRecipe(
                ResourceLocation.fromNamespaceAndPath(CratBatMod.MODID, "cratbat_jei"),  // recipe ID
                "",  // group
                CraftingBookCategory.MISC,
                3,   // width
                3,   // height
                ingredients,
                result
            );

            // Register with JEI
            registration.addRecipes(RecipeTypes.CRAFTING, List.of(cratBatRecipe));
            LOGGER.info("Registered CratBat recipe with JEI");
        } catch (Exception e) {
            LOGGER.error("Failed to register CratBat recipe with JEI", e);
        }

        // Register CrankCrank Doll usage recipe
        try {
            CrankCrankDollRecipe dollRecipe = new CrankCrankDollRecipe();
            registration.addRecipes(CrankCrankDollRecipeCategory.RECIPE_TYPE, List.of(dollRecipe));
            LOGGER.info("Registered CrankCrank Doll usage recipe with JEI");
        } catch (Exception e) {
            LOGGER.error("Failed to register CrankCrank Doll usage recipe with JEI", e);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Register the CrankCrank Doll as a catalyst for its usage recipe
        registration.addRecipeCatalyst(new ItemStack(CratBatMod.CRANKCRANK_DOLL.get()), CrankCrankDollRecipeCategory.RECIPE_TYPE);
        LOGGER.info("Registered CrankCrank Doll as recipe catalyst");
    }
}
