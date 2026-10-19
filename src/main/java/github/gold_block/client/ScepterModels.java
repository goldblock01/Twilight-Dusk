package github.gold_block.client;

import github.gold_block.Config;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 权杖「一套皮肤、两种显示」的烘焙模型登记表。
 *
 * <p>同一根权杖同时存在两份烘焙结果：</p>
 * <ul>
 *   <li><b>平面（flat）</b>——本包手绘的 2D 贴图模型，{@code parent: minecraft:item/generated}；</li>
 *   <li><b>立体（threeD）</b>——BBModel 导出的 3D 模型，走 {@code twilight_dusk:free_rotation} 加载器。</li>
 * </ul>
 *
 * <p>为什么需要这张表：物品栏图标走的是
 * {@code GuiGraphics#renderItem} → {@code ItemRenderer#getModel} → {@code ItemRenderer#render}，
 * 而「用不用 3D 光照」是在 {@code GuiGraphics} 里、调用 {@code render} <b>之前</b>由
 * {@code BakedModel#isGui3d()} / {@code usesBlockLight()} 决定的。若只在 {@code render}
 * 里换模型，几何体换了但光照没换，平面贴图会被当成 3D 模型打光，颜色发暗。
 * 因此必须在 {@code getModel} 取值后立刻换成 GUI 想要的那份 —— 见
 * {@code github.gold_block.mixin.GuiGraphicsMixin}。</p>
 *
 * <p>本类被 {@code event/ScepterRetextureEvents#modifyBakingResult} 在每次模型烘焙时重填，
 * 由混入在每帧渲染物品栏图标时只做一次 Map 查表，无文件 IO。</p>
 */
public final class ScepterModels {

    /** 物品 → 平面 2D 模型 */
    private static final Map<Item, BakedModel> FLAT = new IdentityHashMap<>();

    /** 物品 → 3D 模型 */
    private static final Map<Item, BakedModel> THREE_D = new IdentityHashMap<>();

    private ScepterModels() {
    }

    /** 每次模型烘焙前清空，避免资源重载后残留上一轮的模型引用 */
    public static void reset() {
        FLAT.clear();
        THREE_D.clear();
    }

    /**
     * 登记一根权杖的两份模型。
     *
     * @param item   物品（暮色森林权杖或本模组的炎爆权杖）
     * @param flat   平面模型，可为 {@code null}（烘焙失败时）
     * @param threeD 3D 模型，可为 {@code null}（未注册或烘焙失败时）
     */
    public static void put(Item item, BakedModel flat, BakedModel threeD) {
        if (item == null) {
            return;
        }
        if (flat != null) {
            FLAT.put(item, flat);
        }
        if (threeD != null) {
            THREE_D.put(item, threeD);
        }
    }

    /**
     * 物品栏图标应该用哪份模型。
     *
     * <p>只处理登记过的权杖；其它物品一律原样返回 {@code fallback}。
     * 登记过但需要的模型缺失时也返回 {@code fallback}，绝不返回 {@code null}。</p>
     */
    public static BakedModel guiModel(ItemStack stack, BakedModel fallback) {
        Item item = stack.getItem();
        BakedModel flat = FLAT.get(item);
        if (flat == null) {
            return fallback;
        }
        if (!Config.scepter3dGui()) {
            return flat;
        }
        BakedModel threeD = THREE_D.get(item);
        return threeD != null ? threeD : fallback;
    }
}
