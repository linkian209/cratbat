package xyz.nineworlds.cratbat.entity.client;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xyz.nineworlds.cratbat.CratBatMod;
import xyz.nineworlds.cratbat.entity.TestCratEntity;

/**
 * Simple humanoid model for the TestCrat entity's human form.
 * Uses standard player skin texture format (64x64).
 */
@OnlyIn(Dist.CLIENT)
public class TestCratModel extends HierarchicalModel<TestCratEntity> {

    public static final ModelLayerLocation LAYER_LOCATION =
        new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CratBatMod.MODID, "testcrat"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public TestCratModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDef = new MeshDefinition();
        PartDefinition partDef = meshDef.getRoot();

        partDef.addOrReplaceChild("head",
            CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE),
            PartPose.offset(0.0F, 0.0F, 0.0F));

        partDef.addOrReplaceChild("body",
            CubeListBuilder.create().texOffs(16, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE),
            PartPose.offset(0.0F, 0.0F, 0.0F));

        partDef.addOrReplaceChild("right_arm",
            CubeListBuilder.create().texOffs(40, 16)
                .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
            PartPose.offset(-5.0F, 2.0F, 0.0F));

        partDef.addOrReplaceChild("left_arm",
            CubeListBuilder.create().texOffs(40, 16).mirror()
                .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
            PartPose.offset(5.0F, 2.0F, 0.0F));

        partDef.addOrReplaceChild("right_leg",
            CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
            PartPose.offset(-1.9F, 12.0F, 0.0F));

        partDef.addOrReplaceChild("left_leg",
            CubeListBuilder.create().texOffs(0, 16).mirror()
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE),
            PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshDef, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(TestCratEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        this.rightArm.xRot = (float) Math.cos(limbSwing * 0.6662F + Math.PI) * limbSwingAmount;
        this.leftArm.xRot = (float) Math.cos(limbSwing * 0.6662F) * limbSwingAmount;
        this.rightLeg.xRot = (float) Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = (float) Math.cos(limbSwing * 0.6662F + Math.PI) * 1.4F * limbSwingAmount;
    }
}
