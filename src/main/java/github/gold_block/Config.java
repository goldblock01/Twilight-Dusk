package github.gold_block;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue TWILIGHT_LOOTR_ENABLED = BUILDER
            .define("twilight_lootr_enabled", true);

    public static final ForgeConfigSpec.BooleanValue FIX_WEATHER_COMMAND = BUILDER
            .define("fix_weather_command", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static final Map<String, ForgeConfigSpec.BooleanValue> EDITABLE_OPTIONS;

    static {
        Map<String, ForgeConfigSpec.BooleanValue> options = new LinkedHashMap<>();
        options.put("twilight_lootr_enabled", TWILIGHT_LOOTR_ENABLED);
        options.put("fix_weather_command", FIX_WEATHER_COMMAND);
        EDITABLE_OPTIONS = Collections.unmodifiableMap(options);
    }

    private static ModConfig modConfig;
    private static long lastFileStamp = Long.MIN_VALUE;

    @SubscribeEvent
    static void onLoading(final ModConfigEvent.Loading event) {
        if (isOurConfig(event)) {
            modConfig = event.getConfig();
            lastFileStamp = fileStamp(modConfig);
            TwilightDusk.LOGGER.info("Twilight Dusk config loaded from {}", modConfig.getFullPath());
        }
    }

    @SubscribeEvent
    static void onReloading(final ModConfigEvent.Reloading event) {
        if (isOurConfig(event)) {
            TwilightDusk.LOGGER.info("Twilight Dusk config reloaded, the new values are already in use");
        }
    }

    private static boolean isOurConfig(final ModConfigEvent event) {
        return TwilightDusk.MODID.equals(event.getConfig().getModId());
    }

    public static void reloadIfFileChanged() {
        ModConfig current = modConfig;
        if (current == null) {
            return;
        }
        long stamp = fileStamp(current);
        if (stamp < 0L || stamp == lastFileStamp) {
            return;
        }

        lastFileStamp = stamp;
        try {
            CommentedFileConfig data = CommentedFileConfig.builder(current.getFullPath())
                    .preserveInsertionOrder()
                    .build();
            data.load();
            if (!differs(data)) {

                return;
            }
            SPEC.acceptConfig(data);
            SPEC.afterReload();
            TwilightDusk.LOGGER.info("Twilight Dusk config file changed, the new values are already in use");
        } catch (Exception e) {
            TwilightDusk.LOGGER.warn("Could not read the Twilight Dusk config file, keeping the previous values", e);
        }
    }

    private static boolean differs(final CommentedFileConfig data) {
        for (ForgeConfigSpec.BooleanValue value : EDITABLE_OPTIONS.values()) {
            Object stored = data.get(String.join(".", value.getPath()));
            if (stored instanceof Boolean bool && !bool.equals(read(value))) {
                return true;
            }
        }
        return false;
    }

    private static long fileStamp(final ModConfig config) {
        try {
            return Files.getLastModifiedTime(config.getFullPath()).toMillis();
        } catch (IOException | RuntimeException e) {
            return -1L;
        }
    }

    public static boolean isLoaded() {
        return SPEC.isLoaded();
    }

    public static boolean twilightLootrEnabled() {
        return read(TWILIGHT_LOOTR_ENABLED);
    }

    public static boolean fixWeatherCommand() {
        return read(FIX_WEATHER_COMMAND);
    }

    private static boolean read(final ForgeConfigSpec.BooleanValue value) {

        return SPEC.isLoaded() ? value.get() : value.getDefault();
    }
}
