package xyz.nineworlds.cratbat.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import xyz.nineworlds.cratbat.CratBatMod;

/**
 * Defines custom damage types for the CratBat mod.
 */
public class ModDamageTypes {
    /**
     * Custom damage type for CratBat kills.
     * This damage type is NOT in Vampirism's immortalFromDamageSources list,
     * allowing it to bypass DBNO (Down But Not Out) protection and actually kill vampires.
     */
    public static final ResourceKey<DamageType> CRAT_BAT_STRIKE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(CratBatMod.MODID, "crat_bat_strike")
    );
}