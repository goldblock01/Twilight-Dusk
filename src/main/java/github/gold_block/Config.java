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

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static final List<ForgeConfigSpec.BooleanValue> OPTIONS =
            List.of(TWILIGHT_LOOTR_ENABLED, FIX_WEATHER_COMMAND);

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
