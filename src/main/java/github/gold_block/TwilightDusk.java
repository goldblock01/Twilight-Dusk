package github.gold_block;

import com.mojang.logging.LogUtils;
import github.gold_block.registry.ModBlocks;
import github.gold_block.registry.ModCreativeTabs;
import github.gold_block.registry.ModEntities;
import github.gold_block.registry.ModItems;
import github.gold_block.registry.ModLootModifiers;
import github.gold_block.twilightlootr.init.TwilightLootrRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TwilightDusk.MODID)
public class TwilightDusk {

    public static final String MODID = "twilight_dusk";

    public static final Logger LOGGER = LogUtils.getLogger();

    public TwilightDusk() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModEntities.FROG_VARIANTS.register(modEventBus);
        ModEntities.SENSORS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        TwilightLootrRegistry.register(modEventBus);
        LOGGER.info("Twilight Dusk loaded");
    }
}
