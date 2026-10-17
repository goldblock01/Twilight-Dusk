package github.gold_block.event;

import github.gold_block.Config;
import github.gold_block.TwilightDusk;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ScepterRetextureEvents {

    private static final String[] SCEPTERS = {
            "fortification_scepter",
            "twilight_scepter",
            "zombie_scepter",
            "lifedrain_scepter"};

    @SubscribeEvent
    public static void registerAdditional(ModelEvent.RegisterAdditional event) {
        if (!Config.scepterRetexture()) {
            return;
        }
        for (String name : SCEPTERS) {
            event.register(own(name));
        }
    }

    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
        if (!Config.scepterRetexture()) {
            return;
        }
        Map<ResourceLocation, BakedModel> models = event.getModels();
        for (String name : SCEPTERS) {
            BakedModel model = models.get(own(name));
            if (model != null) {
                models.put(fromTwilightForest(name), model);
            }
        }
    }

    private static ModelResourceLocation own(String name) {
        return new ModelResourceLocation(new ResourceLocation(TwilightDusk.MODID, name), "inventory");
    }

    private static ModelResourceLocation fromTwilightForest(String name) {
        return new ModelResourceLocation(new ResourceLocation("twilightforest", name), "inventory");
    }
}
