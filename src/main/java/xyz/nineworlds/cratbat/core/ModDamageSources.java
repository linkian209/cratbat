package xyz.nineworlds.cratbat.core;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Utility class for creating custom DamageSource instances for the CratBat mod.
 */
public class ModDamageSources {
    private final Registry<DamageType> damageTypes;

    public ModDamageSources(RegistryAccess registryAccess) {
        this.damageTypes = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
    }

    /**
     * Creates a CratBat strike damage source with the specified attacker.
     * This damage source bypasses Vampirism's DBNO protection because it's not
     * in the immortalFromDamageSources config list.
     *
     * @param attacker The entity wielding the CratBat
     * @return A DamageSource for CratBat kills
     */
    public DamageSource cratBatStrike(Entity attacker) {
        return new DamageSource(
                this.damageTypes.getHolderOrThrow(ModDamageTypes.CRAT_BAT_STRIKE),
                attacker
        );
    }

    /**
     * Creates a ModDamageSources instance from a Level.
     *
     * @param level The level to get registry access from
     * @return A new ModDamageSources instance
     */
    public static ModDamageSources from(Level level) {
        return new ModDamageSources(level.registryAccess());
    }
}