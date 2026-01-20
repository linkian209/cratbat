package xyz.nineworlds.cratbat.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * A test entity that alternates between human and bat forms every 10 seconds.
 * Used for testing the CratBat weapon in single-player without requiring
 * another player in bat form.
 *
 * Renders using Vampirism vampire textures for the human form to match
 * the mod's vampire theme.
 */
public class TestCratEntity extends PathfinderMob {

    private static final EntityDataAccessor<Boolean> DATA_BAT_FORM =
        SynchedEntityData.defineId(TestCratEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int FORM_SWITCH_INTERVAL = 200;

    private int formSwitchTimer = 0;

    public TestCratEntity(EntityType<? extends TestCratEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BAT_FORM, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 6.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    public boolean isInBatForm() {
        return this.entityData.get(DATA_BAT_FORM);
    }

    public void setInBatForm(boolean inBatForm) {
        this.entityData.set(DATA_BAT_FORM, inBatForm);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            formSwitchTimer++;
            if (formSwitchTimer >= FORM_SWITCH_INTERVAL) {
                formSwitchTimer = 0;
                setInBatForm(!isInBatForm());
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("InBatForm", isInBatForm());
        tag.putInt("FormSwitchTimer", formSwitchTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setInBatForm(tag.getBoolean("InBatForm"));
        formSwitchTimer = tag.getInt("FormSwitchTimer");
    }
}
