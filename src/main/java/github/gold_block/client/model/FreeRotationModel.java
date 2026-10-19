package github.gold_block.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 允许元素使用任意角度旋转的几何模型。
 *
 * <p>原版 {@link BlockElement.Deserializer#getAngle} 只接受 0 / ±22.5 / ±45，但这个限制
 * <b>只存在于 JSON 反序列化阶段</b>；烘焙层 {@code FaceBakery} 接受任意角度，
 * 顶点变换本身就是通用的四元数旋转。因此这里自己解析元素、把完整欧拉角换算成矩阵，
 * 在 {@link BlockModel#bakeFace} 烘焙出四边形之后再对顶点与法线施加旋转，
 * 从而做到与 Blockbench 里看到的几何完全一致（不烘焙、不丢弃、不近似）。</p>
 *
 * <p>旋转顺序为 <b>ZYX</b>（R = Rz · Ry · Rx），与 Blockbench 元素的欧拉顺序一致。
 * 该顺序由模型自身的镜像对称性反推确定：按 ZYX 合成时对称元素镜像偏差 0.087，
 * 其余五种合成顺序最小为 1.34；同时刃片的 y 跨度与相邻刃片对齐误差 0.094，次优顺序为 2.58。</p>
 *
 * <p><b>适用范围</b>：仅用于 <b>item 模型</b>。item 烘焙时 {@link ModelState} 恒为 identity，
 * 元素矩阵与模型变换可交换；方块模型的 {@code ModelState} 带朝向旋转，会在
 * {@link BlockModel#bakeFace} 内部先施加，与本类的元素矩阵合成顺序相反。</p>
 *
 * <p>JSON 结构（顶层的 {@code elements} 不要写，原版会先按白名单解析它）：</p>
 * <pre>
 * {
 *   "loader": "twilight_dusk:free_rotation",
 *   "textures": { "0": "mod:item/tex", "particle": "#0" },
 *   "display": { ... },
 *   "free_elements": [
 *     { "from": [x,y,z], "to": [x,y,z], "inflate": 0.1, "shade": true,
 *       "rotation": { "origin": [x,y,z], "euler": [rx,ry,rz] },
 *       "faces": { "north": { "uv": [u1,v1,u2,v2], "texture": "#0" } } }
 *   ]
 * }
 * </pre>
 */
public class FreeRotationModel extends SimpleUnbakedGeometry<FreeRotationModel> {

    /** 元素数组的键名，故意不用 {@code elements}，避免被原版反序列化器提前按白名单解析 */
    public static final String ELEMENTS_KEY = "free_elements";

    private final List<Part> parts;

    private FreeRotationModel(List<Part> parts) {
        this.parts = parts;
    }

    /** 一个轴对齐的几何基元，外加需要施加到它身上的旋转矩阵（null 表示不旋转） */
    private record Part(BlockElement element, @Nullable Matrix4f matrix) {
    }

    @Override
    protected void addQuads(IGeometryBakingContext context, IModelBuilder<?> modelBuilder, ModelBaker baker,
                            Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState,
                            ResourceLocation modelLocation) {
        IQuadTransformer postTransform = QuadTransformers.empty();
        var rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) {
            postTransform = UnbakedGeometryHelper.applyRootTransform(modelState, rootTransform);
        }

        for (Part part : this.parts) {
            IQuadTransformer rotate = part.matrix() == null
                    ? QuadTransformers.empty()
                    : QuadTransformers.applying(new Transformation(part.matrix()));

            for (Map.Entry<Direction, BlockElementFace> entry : part.element().faces.entrySet()) {
                Direction direction = entry.getKey();
                BlockElementFace face = entry.getValue();
                TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture));

                // 先在未旋转的轴对齐状态下烘焙：UV 与顶点序都由原版按面方向算好，
                // 之后只移动顶点/法线，贴图与面的对应关系不会串位。
                var quad = BlockModel.bakeFace(part.element(), face, sprite, direction, modelState, modelLocation);
                rotate.processInPlace(quad);
                postTransform.processInPlace(quad);

                if (face.cullForDirection == null) {
                    modelBuilder.addUnculledFace(quad);
                } else {
                    modelBuilder.addCulledFace(modelState.getRotation().rotateTransform(face.cullForDirection), quad);
                }
            }
        }
    }

    /** 几何加载器：把 {@link #ELEMENTS_KEY} 里的元素解析成带旋转矩阵的基元 */
    public static final class Loader implements IGeometryLoader<FreeRotationModel> {

        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public FreeRotationModel read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException {
            if (!json.has(ELEMENTS_KEY)) {
                throw new JsonParseException("free_rotation 模型缺少 \"" + ELEMENTS_KEY + "\" 数组");
            }

            List<Part> parts = new ArrayList<>();
            for (JsonElement raw : GsonHelper.getAsJsonArray(json, ELEMENTS_KEY)) {
                parts.add(readPart(raw.getAsJsonObject(), ctx));
            }
            return new FreeRotationModel(parts);
        }

        private static Part readPart(JsonObject obj, JsonDeserializationContext ctx) {
            Vector3f from = readVector(obj, "from");
            Vector3f to = readVector(obj, "to");
            // 与原版 BlockElement 同口径：先校验原始坐标，再摊 inflate
            checkBounds(from, "from");
            checkBounds(to, "to");
            boolean shade = GsonHelper.getAsBoolean(obj, "shade", true);

            // Blockbench 的 inflate 是六面等距外扩；原版元素没有这个字段，这里摊进坐标
            float inflate = GsonHelper.getAsFloat(obj, "inflate", 0.0F);
            if (inflate != 0.0F) {
                from.sub(inflate, inflate, inflate);
                to.add(inflate, inflate, inflate);
            }

            Map<Direction, BlockElementFace> faces = new LinkedHashMap<>();
            JsonObject faceJson = GsonHelper.getAsJsonObject(obj, "faces");
            for (Map.Entry<String, JsonElement> entry : faceJson.entrySet()) {
                Direction direction = Direction.byName(entry.getKey());
                if (direction == null) {
                    throw new JsonParseException("未知面方向: " + entry.getKey());
                }
                // 面写成 null 表示该面不渲染，跳过；不塞 null 进表，避免烘焙期空指针
                if (entry.getValue().isJsonNull()) {
                    continue;
                }
                BlockElementFace face = ctx.deserialize(entry.getValue(), BlockElementFace.class);
                if (face != null) {
                    faces.put(direction, face);
                }
            }
            if (faces.isEmpty()) {
                throw new JsonParseException("free_rotation 元素至少要有一个面");
            }

            Matrix4f matrix = null;
            JsonElement rotation = obj.get("rotation");
            if (rotation != null && !rotation.isJsonNull()) {
                JsonObject rot = rotation.getAsJsonObject();
                // origin 与原版一致，按 0~16 空间书写，内部换算到 0~1
                Vector3f origin = readVector(rot, "origin").mul(1.0F / 16.0F);
                Vector3f euler = readVector(rot, "euler");
                matrix = eulerMatrix(origin, euler);
            }

            // 基元本身保持轴对齐；旋转由上面的矩阵在烘焙后统一施加
            return new Part(new BlockElement(from, to, faces, null, shade), matrix);
        }

        /**
         * 欧拉角换算成绕 origin 的旋转矩阵：M = T(origin) · Rz · Ry · Rx · T(-origin)。
         * 三个 Matrix4f.rotateX/Y/Z 都是右乘，所以按 Z → Y → X 的顺序调用即为 ZYX 合成。
         */
        private static Matrix4f eulerMatrix(Vector3f origin, Vector3f euler) {
            return new Matrix4f()
                    .translate(origin.x, origin.y, origin.z)
                    .rotateZ(euler.z * Mth.DEG_TO_RAD)
                    .rotateY(euler.y * Mth.DEG_TO_RAD)
                    .rotateX(euler.x * Mth.DEG_TO_RAD)
                    .translate(-origin.x, -origin.y, -origin.z);
        }

        /** 每个分量必须落在 [-16, 32]；与原版一致，允许 from 与 to 相等（零厚面） */
        private static void checkBounds(Vector3f v, String key) {
            if (v.x < -16.0F || v.y < -16.0F || v.z < -16.0F
                    || v.x > 32.0F || v.y > 32.0F || v.z > 32.0F) {
                throw new JsonParseException(
                        "\"" + key + "\" 超出允许范围 [-16, 32]: " + v);
            }
        }

        private static Vector3f readVector(JsonObject obj, String key) {
            JsonArray array = GsonHelper.getAsJsonArray(obj, key);
            if (array.size() != 3) {
                throw new JsonParseException(key + " 需要 3 个分量，实际 " + array.size());
            }
            return new Vector3f(
                    GsonHelper.convertToFloat(array.get(0), key + "[0]"),
                    GsonHelper.convertToFloat(array.get(1), key + "[1]"),
                    GsonHelper.convertToFloat(array.get(2), key + "[2]"));
        }
    }
}
