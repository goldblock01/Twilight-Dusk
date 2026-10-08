package github.gold_block.client;

import github.gold_block.TwilightDusk;
import github.gold_block.item.flask.FlaskBehavior;
import github.gold_block.item.flask.FlaskTooltip;
import github.gold_block.registry.ModBlocks;
import github.gold_block.registry.ModEntities;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import twilightforest.init.TFItems;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DUSK_FROG.get(), FrogRenderer::new);
        event.registerEntityRenderer(ModEntities.DUSK_TADPOLE.get(), DuskTadpoleRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(FlaskTooltip.class, FlaskTooltipComponent::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(ModBlocks.DUSK_FROGSPAWN.get(), RenderType.cutout()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        ItemProperties.register(TFItems.BRITTLE_FLASK.get(), POTION_LEVEL,
                (stack, level, entity, seed) -> FlaskBehavior.modelLevel(stack));
        ItemProperties.register(TFItems.GREATER_FLASK.get(), POTION_LEVEL,
                (stack, level, entity, seed) -> FlaskBehavior.modelLevel(stack));
    }

    private static final ResourceLocation POTION_LEVEL = new ResourceLocation("twilightforest", "potion_level");
}
