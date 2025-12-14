package xyz.nineworlds.cratbat.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class CratBatItem extends SwordItem {
    public CratBatItem(Properties properties) {
        // Same stats as wooden sword: 4 damage, -2.4 attack speed
        super(new CratBatTier(), 3, -2.4f, properties);
    }

    private static class CratBatTier implements Tier {
        @Override
        public int getUses() {
            return 59; // Same as wooden tools
        }

        @Override
        public float getSpeed() {
            return 2.0F; // Same as wood
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0F; // Same as wooden tier
        }

        @Override
        public int getLevel() {
            return 0; // Same as wood
        }

        @Override
        public int getEnchantmentValue() {
            return 15; // Same as wood
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY; // Cannot be repaired
        }
    }
}