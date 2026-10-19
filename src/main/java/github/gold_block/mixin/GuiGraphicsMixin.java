package github.gold_block.mixin;

import github.gold_block.client.ScepterModels;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 让权杖在物品栏里可以「2D 图标、3D 模型」分开配置。
 *
 * <p>为什么要动这里：物品栏图标的渲染顺序是
 * {@code GuiGraphics#renderItem} → {@code ItemRenderer#getModel} 取模型 →
 * 用 {@code BakedModel#isGui3d()} / {@code usesBlockLight()} 决定 z 偏移与光照 →
 * 最后才 {@code ItemRenderer#render} 画几何体。</p>
 *
 * <p>如果只在 {@code render} 里换模型，几何体换成了 2D 平面，但光照已经被 3D 模型
 * 决定成「立体物品光照」，平面图标会明显发暗；反过来也一样。所以必须在这里、
 * 即 {@code getModel} 返回的那一刻就把模型换掉，让后面的 {@code isGui3d()}
 * 与光照都基于「真正要画的那份模型」来判断。</p>
 *
 * <p>只影响物品栏 / 快捷栏 / JEI 列表等 GUI 场景；手持、掉落物、展示框走的是
 * {@code ItemRenderer#renderStatic} → {@code getModel}，不经过这里，
 * 用的是 {@code ScepterRetextureEvents} 挂进槽位的那份模型（由
 * {@code scepter_3d_world} 决定），两边互不干扰。</p>
 *
 * <p>🔴 为什么用 {@code @Redirect} 而不是 {@code @ModifyVariable}：原版类不带局部变量表
 * （LVT），{@code @ModifyVariable(at = STORE)} 按类型找不到变量，
 * 会以 {@code failed injection check, (0/1)} 在启动阶段崩掉。{@code @Redirect}
 * 只依赖方法调用指令，不依赖 LVT。</p>
 */
@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

    /**
     * 把 {@code GuiGraphics#renderItem} 里取模型的那次调用换成「GUI 该用的模型」。
     *
     * <p>目标方法（1.20.1 原版私有方法）：
     * {@code renderItem(LivingEntity, Level, ItemStack, x, y, seed, zOffset)}。
     * 被重定向的调用：
     * {@code ItemRenderer#getModel(ItemStack, Level, LivingEntity, int)}。</p>
     */
    @Redirect(
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;"
                    + "Lnet/minecraft/world/item/ItemStack;IIII)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getModel("
                            + "Lnet/minecraft/world/item/ItemStack;"
                            + "Lnet/minecraft/world/level/Level;"
                            + "Lnet/minecraft/world/entity/LivingEntity;"
                            + "I)Lnet/minecraft/client/resources/model/BakedModel;"))
    private BakedModel twilight_dusk$guiModel(ItemRenderer renderer, ItemStack stack, Level level,
                                              LivingEntity entity, int seed) {
        return ScepterModels.guiModel(stack, renderer.getModel(stack, level, entity, seed));
    }
}
