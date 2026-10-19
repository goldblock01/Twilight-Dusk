package github.gold_block;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue TWILIGHT_LOOTR_ENABLED = BUILDER
            .define("twilight_lootr_enabled", true);

    public static final ForgeConfigSpec.BooleanValue FIX_WEATHER_COMMAND = BUILDER
            .define("fix_weather_command", true);

    public static final ForgeConfigSpec.BooleanValue PHANTOM_KNIGHT_TOOLS = BUILDER
            .define("phantom_knight_tools", true);

    public static final ForgeConfigSpec.BooleanValue SCEPTER_RETEXTURE = BUILDER
            .define("scepter_retexture", true);

    /**
     * 权杖在**物品栏 / GUI**（背包、快捷栏、JEI 列表等）里的图标是否使用 3D 模型。
     *
     * <p>默认 {@code false}：用本包手绘的 2D 平面贴图，图标填满格子、辨识度更好。
     * 改为 {@code true} 则用 3D 模型按投影显示（会比 2D 略小）。</p>
     */
    public static final ForgeConfigSpec.BooleanValue SCEPTER_3D_GUI = BUILDER
            .comment("权杖在物品栏（背包 / 快捷栏 / JEI 列表）里的图标是否使用 3D 模型。",
                    "false（默认）＝ 使用本包手绘的 2D 平面贴图，图标填满格子；",
                    "true        ＝ 使用 3D 模型，图标按模型投影显示（会比 2D 略小）。",
                    "本项与 scepter_3d_world 相互独立，可以「物品栏 2D ＋ 手持 3D」。")
            .define("scepter_3d_gui", false);

    /**
     * 权杖在**世界里**的场景（手持、掉落物、物品展示框）是否使用 3D 模型。
     *
     * <p>默认 {@code true}：这些场景看的是立体造型，3D 模型才对。
     * 改为 {@code false} 则全部退回 2D 平面贴图。</p>
     */
    public static final ForgeConfigSpec.BooleanValue SCEPTER_3D_WORLD = BUILDER
            .comment("权杖在手持、掉落物、物品展示框等非物品栏场景是否使用 3D 模型。",
                    "true（默认）＝ 使用 3D 模型；",
                    "false       ＝ 退回 2D 平面贴图。",
                    "本项与 scepter_3d_gui 相互独立。")
            .define("scepter_3d_world", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static final List<ForgeConfigSpec.BooleanValue> OPTIONS =
            List.of(TWILIGHT_LOOTR_ENABLED, FIX_WEATHER_COMMAND, PHANTOM_KNIGHT_TOOLS, SCEPTER_RETEXTURE,
                    SCEPTER_3D_GUI, SCEPTER_3D_WORLD);

    private static final Path FILE =
            FMLPaths.CONFIGDIR.get().resolve(TwilightDusk.MODID + "-common.toml");

    private static long lastModified = Long.MIN_VALUE;

    private Config() {
    }

    public static boolean twilightLootrEnabled() {
        return read(TWILIGHT_LOOTR_ENABLED);
    }

    public static boolean fixWeatherCommand() {
        return read(FIX_WEATHER_COMMAND);
    }

    public static boolean phantomKnightTools() {
        return read(PHANTOM_KNIGHT_TOOLS);
    }

    public static boolean scepterRetexture() {
        return read(SCEPTER_RETEXTURE);
    }

    /** 权杖在物品栏图标里是否用 3D 模型（默认 false ⇒ 用 2D 平面贴图） */
    public static boolean scepter3dGui() {
        return read(SCEPTER_3D_GUI);
    }

    /** 权杖在手持 / 掉落物 / 展示框等世界场景是否用 3D 模型（默认 true） */
    public static boolean scepter3dWorld() {
        return read(SCEPTER_3D_WORLD);
    }

    public static void reloadIfFileChanged() {
        long modified = fileModifiedTime();
        if (modified == lastModified) {
            return;
        }
        lastModified = modified;
        reload();
    }

    private static void reload() {
        CommentedFileConfig file = CommentedFileConfig.builder(FILE).preserveInsertionOrder().build();
        try {
            file.load();
            if (!anyChanged(file)) {
                return;
            }
            SPEC.acceptConfig(file);
            SPEC.afterReload();
        } catch (Exception ignored) {
        }
    }

    private static boolean anyChanged(final CommentedFileConfig file) {
        return OPTIONS.stream().anyMatch(option -> {
            Object stored = file.get(String.join(".", option.getPath()));
            return stored instanceof Boolean value && !value.equals(read(option));
        });
    }

    private static long fileModifiedTime() {
        try {
            return Files.getLastModifiedTime(FILE).toMillis();
        } catch (IOException | RuntimeException e) {
            return -1L;
        }
    }

    private static boolean read(final ForgeConfigSpec.BooleanValue option) {
        Boolean value = SPEC.isLoaded() ? option.get() : null;
        return value != null ? value : option.getDefault();
    }
}
