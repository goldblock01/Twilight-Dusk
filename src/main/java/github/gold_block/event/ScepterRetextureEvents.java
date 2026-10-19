package github.gold_block.event;

import github.gold_block.Config;
import github.gold_block.TwilightDusk;
import github.gold_block.client.ScepterModels;
import github.gold_block.registry.ModItems;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * 权杖外观替换：把平面 / 3D 两份模型按配置挂到物品槽位上。
 *
 * <p>物品共有两类：</p>
 * <ul>
 *   <li><b>暮色森林的四把权杖</b>——外观由本包「顶替」，受 {@code scepter_retexture} 总开关控制；</li>
 *   <li><b>本模组自己的炎爆权杖</b>——只换它自己的模型，不受 {@code scepter_retexture} 影响。</li>
 * </ul>
 *
 * <p>每根权杖都有「平面」与「3D」两份模型。落到槽位上的那一份由
 * {@code scepter_3d_world}（手持 / 掉落物 / 展示框）决定；物品栏图标那一份由
 * {@code scepter_3d_gui} 决定，在渲染时由 {@link ScepterModels#guiModel} ＋
 * {@code GuiGraphicsMixin} 临时替换。两个开关互不影响，可以「物品栏 2D ＋ 手持 3D」。</p>
 */
@Mod.EventBusSubscriber(modid = TwilightDusk.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ScepterRetextureEvents {

    /** 外观被顶替的暮色权杖；名字与暮色森林的物品 id 一致 */
    private static final String[] TWILIGHT_SCEPTERS = {
            "fortification_scepter",
            "twilight_scepter",
            "zombie_scepter",
            "lifedrain_scepter"};

    /** 本模组自带的权杖，只把它自己的模型换成 3D */
    private static final String BURST_SCEPTER = "burst_scepter";

    /** 3D 模型相对平面模型的文件名后缀 */
    private static final String MODEL_3D_SUFFIX = "_3d";

    private static final String TWILIGHT_FOREST = "twilightforest";

    /**
     * 把需要的模型登记进烘焙队列。
     *
     * <p>平面模型也要登记：暮色权杖的平面模型（{@code twilight_dusk:zombie_scepter} 等）
     * 没有对应的物品，不主动登记就不会被烘焙，物品栏的 2D 图标就取不到它。</p>
     */
    @SubscribeEvent
    public static void registerAdditional(ModelEvent.RegisterAdditional event) {
        boolean any3d = Config.scepter3dGui() || Config.scepter3dWorld();
        if (Config.scepterRetexture()) {
            for (String name : TWILIGHT_SCEPTERS) {
                event.register(own(name));
                if (any3d) {
                    event.register(threeD(name));
                }
            }
        }
        if (any3d) {
            event.register(threeD(BURST_SCEPTER));
        }
    }

    /**
     * 烘焙完成后，把选定的模型放进物品槽位，同时把两份模型登记给
     * {@link ScepterModels}，供物品栏渲染时切换。
     */
    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<ResourceLocation, BakedModel> models = event.getModels();
        ScepterModels.reset();

        boolean any3d = Config.scepter3dGui() || Config.scepter3dWorld();
        if (!any3d) {
            return;
        }
        boolean world3d = Config.scepter3dWorld();

        if (Config.scepterRetexture()) {
            for (String name : TWILIGHT_SCEPTERS) {
                apply(models, own(name), threeD(name), fromTwilightForest(name),
                        item(TWILIGHT_FOREST, name), world3d);
            }
        }
        // 炎爆权杖的平面模型就是它自己的物品模型，来源与槽位是同一个位置
        apply(models, own(BURST_SCEPTER), threeD(BURST_SCEPTER), own(BURST_SCEPTER),
                ModItems.BURST_SCEPTER.get(), world3d);
    }

    /**
     * 取一份平面 / 一份 3D，登记进 {@link ScepterModels}，并把「世界场景」该用的那份写进槽位。
     *
     * @param flat    平面模型的位置
     * @param threeD  3D 模型的位置
     * @param target  最终被物品引用的槽位
     * @param item    物品实例（渲染时用物品反查模型）
     * @param world3d 世界场景是否要 3D；{@code false} 或 3D 缺失时都退回平面
     */
    private static void apply(Map<ResourceLocation, BakedModel> models,
                              ModelResourceLocation flat,
                              ModelResourceLocation threeD,
                              ModelResourceLocation target,
                              Item item,
                              boolean world3d) {
        BakedModel flatModel = models.get(flat);
        BakedModel threeDModel = models.get(threeD);
        if (flatModel == null && threeDModel == null) {
            return;
        }
        ScepterModels.put(item, flatModel, threeDModel);

        BakedModel chosen = world3d && threeDModel != null ? threeDModel : flatModel;
        if (chosen != null) {
            models.put(target, chosen);
        }
    }

    private static Item item(String namespace, String name) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, name));
    }

    private static ModelResourceLocation own(String name) {
        return new ModelResourceLocation(new ResourceLocation(TwilightDusk.MODID, name), "inventory");
    }

    private static ModelResourceLocation threeD(String name) {
        return own(name + MODEL_3D_SUFFIX);
    }

    private static ModelResourceLocation fromTwilightForest(String name) {
        return new ModelResourceLocation(new ResourceLocation(TWILIGHT_FOREST, name), "inventory");
    }
}
