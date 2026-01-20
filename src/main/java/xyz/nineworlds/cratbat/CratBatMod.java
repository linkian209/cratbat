package xyz.nineworlds.cratbat;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import xyz.nineworlds.cratbat.command.CratBatCommand;
import xyz.nineworlds.cratbat.entity.TestCratEntity;
import xyz.nineworlds.cratbat.event.CratBatEventHandler;
import xyz.nineworlds.cratbat.item.BatWingItem;
import xyz.nineworlds.cratbat.item.CratBatItem;
import xyz.nineworlds.cratbat.item.CratBatShieldItem;
import xyz.nineworlds.cratbat.item.CrankCrankDollItem;
import xyz.nineworlds.cratbat.item.TestCratSpawnerItem;
import xyz.nineworlds.cratbat.network.CratBatNetwork;
import xyz.nineworlds.cratbat.recipe.CratBatRecipeSerializer;

@Mod(CratBatMod.MODID)
public class CratBatMod {
    public static final String MODID = "cratbat";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public static final RegistryObject<Item> CRATBAT = ITEMS.register("cratbat",
        () -> new CratBatItem(new Item.Properties()));

    public static final RegistryObject<Item> CRANKCRANK_DOLL = ITEMS.register("crankcrank_doll",
        () -> new CrankCrankDollItem(new Item.Properties()));

    public static final RegistryObject<Item> BAT_WING = ITEMS.register("bat_wing",
        () -> new BatWingItem(new Item.Properties()));

    public static final RegistryObject<Item> CRATBAT_SHIELD = ITEMS.register("cratbat_shield",
        () -> new CratBatShieldItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TEST_CRAT_SPAWNER = ITEMS.register("testcrat_spawner",
        () -> new TestCratSpawnerItem(new Item.Properties()));

    public static final RegistryObject<RecipeSerializer<?>> CRATBAT_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("cratbat_recipe",
        () -> CratBatRecipeSerializer.INSTANCE);

    public static final RegistryObject<EntityType<TestCratEntity>> TEST_CRAT_ENTITY = ENTITY_TYPES.register("testcrat",
        () -> EntityType.Builder.of(TestCratEntity::new, MobCategory.CREATURE)
            .sized(0.6F, 1.8F)
            .build("testcrat"));

    public static final RegistryObject<CreativeModeTab> CRATBAT_TAB = CREATIVE_MODE_TABS.register("cratbat_tab",
        () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> CRATBAT.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(CRATBAT.get());
                output.accept(CRANKCRANK_DOLL.get());
                output.accept(BAT_WING.get());
                output.accept(CRATBAT_SHIELD.get());
                if (CratBatConfig.isTestCratEnabledFromSpec()) {
                    output.accept(TEST_CRAT_SPAWNER.get());
                }
            }).build());

    public CratBatMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CratBatEventHandler());

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::registerEntityAttributes);

        context.registerConfig(ModConfig.Type.COMMON, CratBatConfig.SPEC);
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        // Always register attributes since entity type is always registered
        // The entity just won't be spawnable if config is disabled
        event.put(TEST_CRAT_ENTITY.get(), TestCratEntity.createAttributes().build());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CratBatNetwork.register();
            LOGGER.info("CratBat network channel registered");
        });
        LOGGER.info("CratBat mod initialized!");
        LOGGER.info("Ready to bat the crat out of the sky!");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(CRATBAT);
            event.accept(CRANKCRANK_DOLL);
            event.accept(BAT_WING);
            event.accept(CRATBAT_SHIELD);
        }
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("CratBat server starting");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CratBatCommand.register(event.getDispatcher());
    }
}