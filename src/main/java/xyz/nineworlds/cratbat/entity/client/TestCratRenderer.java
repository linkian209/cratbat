package xyz.nineworlds.cratbat.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.nineworlds.cratbat.entity.TestCratEntity;

/**
 * Renderer for TestCrat entity that switches between bat and humanoid models
 * based on the entity's current form.
 */
@OnlyIn(Dist.CLIENT)
public class TestCratRenderer extends MobRenderer<TestCratEntity, TestCratModel> {

    private static final ResourceLocation BAT_TEXTURE =
        ResourceLocation.withDefaultNamespace("textures/entity/bat.png");
    private static final ResourceLocation VAMPIRE_TEXTURE =
        ResourceLocation.fromNamespaceAndPath("vampirism", "textures/entity/vampire/vampire1.png");

    private final TestCratBatModel batModel;
    private final TestCratModel humanModel;

    public TestCratRenderer(EntityRendererProvider.Context context) {
        super(context, new TestCratModel(context.bakeLayer(TestCratModel.LAYER_LOCATION)), 0.5F);
        this.humanModel = this.model;
        this.batModel = new TestCratBatModel(context.bakeLayer(TestCratBatModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(TestCratEntity entity) {
        if (entity.isInBatForm()) {
            return BAT_TEXTURE;
        }
        return VAMPIRE_TEXTURE;
    }

    @Override
    public void render(TestCratEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isInBatForm()) {
            renderBatForm(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        } else {
            this.model = this.humanModel;
            this.shadowRadius = 0.5F;
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    private void renderBatForm(TestCratEntity entity, float entityYaw, float partialTicks,
                               PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float f1 = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        float f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float f2 = f1 - f;

        float f6 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        float f7 = this.getBob(entity, partialTicks);

        float f5 = 0.0F;
        float f8 = 0.0F;

        if (entity.isAlive()) {
            f5 = entity.walkAnimation.speed(partialTicks);
            f8 = entity.walkAnimation.position(partialTicks);
            if (f5 > 1.0F) {
                f5 = 1.0F;
            }
        }

        poseStack.translate(0.0F, Mth.cos(f7 * 0.3F) * 0.1F, 0.0F);

        poseStack.scale(0.35F, 0.35F, 0.35F);

        poseStack.translate(0.0F, 1.5F, 0.0F);

        this.batModel.prepareMobModel(entity, f8, f5, partialTicks);
        this.batModel.setupAnim(entity, f8, f5, f7, f2, f6);

        RenderType renderType = RenderType.entityCutoutNoCull(BAT_TEXTURE);
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);

        this.batModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    @Override
    protected void scale(TestCratEntity entity, PoseStack poseStack, float partialTicks) {
        if (!entity.isInBatForm()) {
            poseStack.scale(0.9375F, 0.9375F, 0.9375F);
        }
    }
}
