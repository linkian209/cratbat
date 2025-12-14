package xyz.nineworlds.cratbat.integration.jei;

import net.minecraft.world.item.ItemStack;
import xyz.nineworlds.cratbat.CratBatMod;
import xyz.nineworlds.cratbat.item.CrankSkullItem;

/**
 * JEI recipe wrapper for showing how to use the CrankCrank Doll
 */
public class CrankCrankDollRecipe {
    private final ItemStack doll;
    private final ItemStack playerRepresentation;
    private final ItemStack result;

    public CrankCrankDollRecipe() {
        this.doll = new ItemStack(CratBatMod.CRANKCRANK_DOLL.get());
        // Use the Crank Skull (which is a player head) to represent the target player
        this.playerRepresentation = CrankSkullItem.createCrankSkull();
        this.result = CrankSkullItem.createCrankSkull();
    }

    public ItemStack getDoll() {
        return doll;
    }

    public ItemStack getPlayerRepresentation() {
        return playerRepresentation;
    }

    public ItemStack getResult() {
        return result;
    }
}