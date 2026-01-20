package xyz.nineworlds.cratbat.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import xyz.nineworlds.cratbat.CratBatMod;
import xyz.nineworlds.cratbat.entity.TestCratEntity;

/**
 * Item that spawns a TestCrat entity when used on a block.
 * Used for testing the CratBat weapon in single-player.
 */
public class TestCratSpawnerItem extends Item {

    public TestCratSpawnerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (!level.isClientSide) {
            BlockPos clickedPos = context.getClickedPos();
            BlockPos spawnPos = clickedPos.relative(context.getClickedFace());

            TestCratEntity entity = CratBatMod.TEST_CRAT_ENTITY.get().create(level);
            if (entity != null) {
                entity.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                level.addFreshEntity(entity);

                if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
