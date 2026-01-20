package xyz.nineworlds.cratbat.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.nineworlds.cratbat.CratBatMod;
import xyz.nineworlds.cratbat.entity.client.TestCratBatModel;
import xyz.nineworlds.cratbat.entity.client.TestCratModel;
import xyz.nineworlds.cratbat.entity.client.TestCratRenderer;

/**
 * Client-side setup for entity renderers and model layer definitions.
 */
@Mod.EventBusSubscriber(modid = CratBatMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CratBatMod.TEST_CRAT_ENTITY.get(), TestCratRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TestCratModel.LAYER_LOCATION, TestCratModel::createBodyLayer);
        event.registerLayerDefinition(TestCratBatModel.LAYER_LOCATION, TestCratBatModel::createBodyLayer);
    }
}
