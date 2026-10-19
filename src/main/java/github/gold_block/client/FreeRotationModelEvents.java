package github.gold_block.client;

import github.gold_block.TwilightDusk;
import github.gold_block.client.model.FreeRotationModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 注册自定义几何加载器 {@code twilight_dusk:free_rotation}。
 *
 * <p>原版元素的 JSON 反序列化只接受 0 / ±22.5 / ±45 三种角度，而模型文件里的
 * 5°、10°、85° 这类角度无法用原版 elements 表达。该加载器绕过反序列化阶段的限制，
 * 让模型可以按 Blockbench 里的真实角度渲染。</p>
 */
@Mod.EventBusSubscriber(modid = TwilightDusk.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FreeRotationModelEvents {

    /** 模型 json 里写 "loader": "twilight_dusk:free_rotation" 即使用该加载器 */
    private static final String FREE_ROTATION = "free_rotation";

    private FreeRotationModelEvents() {
    }

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(FREE_ROTATION, FreeRotationModel.Loader.INSTANCE);
    }
}
