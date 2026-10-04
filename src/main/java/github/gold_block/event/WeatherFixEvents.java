package github.gold_block.event;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.context.StringRange;
import github.gold_block.Config;
import github.gold_block.TwilightDusk;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public class WeatherFixEvents {

    private static final ResourceKey<Level> TWILIGHT_FOREST =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation("twilightforest", "twilight_forest"));

    private static final String ARG_DURATION = "duration";
    private static final String ARG_DIMENSION = "dimension";

    private static final Pattern EXECUTE_IN = Pattern.compile("\\bin\\s+([a-z0-9_.-]+:[a-z0-9_/.-]+)");

    private static final int MIRROR_INTERVAL = 10;

    private static final int RAIN_MIN = 12000;
    private static final int RAIN_MAX = 24000;
    private static final int THUNDER_MIN = 3600;
    private static final int THUNDER_MAX = 15600;
    private static final int CLEAR_MIN = 12000;
    private static final int CLEAR_MAX = 180000;

    private static boolean active = false;
    private static int rainTime = 0;
    private static int thunderTime = 0;
    private static int clearTime = 0;

    private static boolean controlling = false;

    private static boolean lastRaining = false;
    private static boolean lastThundering = false;

    private static int mirrorCountdown = 0;

    private static final Set<UUID> syncedPlayers = new HashSet<>();

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {
        if (!Config.fixWeatherCommand()) {
            return;
        }
        ParseResults<CommandSourceStack> parse = event.getParseResults();
        if (parse == null) {
            return;
        }
        CommandContextBuilder<CommandSourceStack> builder = parse.getContext();
        if (builder == null || builder.getSource() == null) {
            return;
        }
        String input = parse.getReader().getString();

        String type = resolveWeatherType(builder);
        if (type == null) {
            return;
        }
        if (!targetsTwilightForest(builder, input)) {
            return;
        }

        ServerLevel level = builder.getSource().getServer().getLevel(TWILIGHT_FOREST);
        if (level == null) {
            return;
        }

        RandomSource random = level.getRandom();
        int duration = resolveDuration(builder, input);

        switch (type) {
            case "clear" -> {
                clearTime = duration >= 0 ? duration : randomBetween(random, CLEAR_MIN, CLEAR_MAX);
                rainTime = 0;
                thunderTime = 0;
            }
            case "rain" -> {
                rainTime = duration >= 0 ? duration : randomBetween(random, RAIN_MIN, RAIN_MAX);
                clearTime = 0;
            }
            case "thunder" -> {
                rainTime = duration >= 0 ? duration : randomBetween(random, RAIN_MIN, RAIN_MAX);
                thunderTime = duration >= 0 ? duration : randomBetween(random, THUNDER_MIN, THUNDER_MAX);
                clearTime = 0;
            }
            default -> {
                return;
            }
        }
        active = true;
        mirrorCountdown = 0;
        pushWeather(level, rainTime > 0, rainTime > 0 && thunderTime > 0);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = event.getServer();

        if (!Config.fixWeatherCommand()) {
            release(server);
            return;
        }
        ServerLevel level = server.getLevel(TWILIGHT_FOREST);
        if (level == null) {
            return;
        }

        boolean raining;
        boolean thundering;
        if (active) {
            advancePlayback(level);
            raining = rainTime > 0;
            thundering = raining && thunderTime > 0;
            pushWeather(level, raining, thundering);
        } else {
            raining = level.getLevelData().isRaining();
            thundering = level.getLevelData().isThundering();
            if (--mirrorCountdown <= 0) {
                mirrorCountdown = MIRROR_INTERVAL;
                pushWeather(level, raining, thundering);
            }
        }

        welcomeNewcomers(level, raining, thundering);
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        reset();
    }

    private static void advancePlayback(ServerLevel level) {
        if (rainTime > 0) {
            rainTime--;
            if (thunderTime > 0) {
                thunderTime--;
            }
            if (rainTime == 0) {
                clearTime = randomBetween(level.getRandom(), CLEAR_MIN, CLEAR_MAX);
                thunderTime = 0;
            }
        } else if (clearTime > 0) {
            clearTime--;
            if (clearTime == 0) {
                active = false;
                mirrorCountdown = 0;
            }
        } else {
            active = false;
            mirrorCountdown = 0;
        }
    }

    private static void release(MinecraftServer server) {
        if (!controlling) {
            return;
        }
        reset();

        ServerLevel level = server.getLevel(TWILIGHT_FOREST);
        if (level == null) {
            return;
        }
        boolean raining = level.getLevelData().isRaining();
        boolean thundering = level.getLevelData().isThundering();

        level.setRainLevel(raining ? 1.0F : 0.0F);
        level.setThunderLevel(raining && thundering ? 1.0F : 0.0F);
        for (ServerPlayer player : level.players()) {
            sendWeather(player, raining, thundering);
        }
    }

    private static void reset() {
        active = false;
        controlling = false;
        rainTime = 0;
        thunderTime = 0;
        clearTime = 0;
        lastRaining = false;
        lastThundering = false;
        mirrorCountdown = 0;
        syncedPlayers.clear();
    }

    private static void pushWeather(ServerLevel level, boolean raining, boolean thundering) {
        boolean rainChanged = raining != lastRaining;
        boolean thunderChanged = thundering != lastThundering;
        lastRaining = raining;
        lastThundering = thundering;

        level.setRainLevel(raining ? 1.0F : 0.0F);
        level.setThunderLevel(raining && thundering ? 1.0F : 0.0F);
        controlling = true;

        if (!rainChanged && !thunderChanged) {
            return;
        }
        for (ServerPlayer player : level.players()) {
            sendWeather(player, raining, thundering);
        }
    }

    private static void welcomeNewcomers(ServerLevel level, boolean raining, boolean thundering) {
        List<ServerPlayer> players = level.players();
        for (ServerPlayer player : players) {
            if (syncedPlayers.add(player.getUUID())) {
                sendWeather(player, raining, thundering);
            }
        }
        if (syncedPlayers.size() > players.size()) {
            Set<UUID> present = new HashSet<>(players.size() * 2);
            for (ServerPlayer player : players) {
                present.add(player.getUUID());
            }
            syncedPlayers.retainAll(present);
        }
    }

    private static void sendWeather(ServerPlayer player, boolean raining, boolean thundering) {
        player.connection.send(new ClientboundGameEventPacket(
                raining ? ClientboundGameEventPacket.START_RAINING : ClientboundGameEventPacket.STOP_RAINING, 0.0F));
        player.connection.send(new ClientboundGameEventPacket(
                ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, raining ? 1.0F : 0.0F));
        player.connection.send(new ClientboundGameEventPacket(
                ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, raining && thundering ? 1.0F : 0.0F));
    }

    private static boolean targetsTwilightForest(CommandContextBuilder<CommandSourceStack> builder, String input) {

        String explicit = findExplicitDimension(builder, input);
        if (explicit != null) {
            ResourceLocation id = ResourceLocation.tryParse(explicit);
            return id != null && id.equals(TWILIGHT_FOREST.location());
        }
        CommandSourceStack source = builder.getSource();
        return source != null && source.getLevel() != null
                && source.getLevel().dimension().equals(TWILIGHT_FOREST);
    }

    private static String findExplicitDimension(CommandContextBuilder<CommandSourceStack> builder, String input) {
        String found = null;
        for (ParsedCommandNode<CommandSourceStack> parsed : allNodes(builder)) {
            if (!parsed.getNode().getName().equals(ARG_DIMENSION)) {
                continue;
            }
            StringRange range = parsed.getRange();
            if (range.getStart() < 0 || range.getEnd() > input.length()) {
                continue;
            }

            found = input.substring(range.getStart(), range.getEnd());
        }
        if (found != null) {
            return found;
        }
        Matcher matcher = EXECUTE_IN.matcher(input);
        while (matcher.find()) {
            found = matcher.group(1);
        }
        return found;
    }

    private static List<ParsedCommandNode<CommandSourceStack>> allNodes(CommandContextBuilder<CommandSourceStack> builder) {
        List<ParsedCommandNode<CommandSourceStack>> nodes = new ArrayList<>();
        for (CommandContextBuilder<CommandSourceStack> current = builder; current != null; current = current.getChild()) {
            nodes.addAll(current.getNodes());
        }
        return nodes;
    }

    private static String resolveWeatherType(CommandContextBuilder<CommandSourceStack> builder) {
        for (ParsedCommandNode<CommandSourceStack> parsed : allNodes(builder)) {
            String name = parsed.getNode().getName();
            if (name.equals("clear") || name.equals("rain") || name.equals("thunder")) {
                return name;
            }
        }
        return null;
    }

    private static int resolveDuration(CommandContextBuilder<CommandSourceStack> builder, String input) {
        for (CommandContextBuilder<CommandSourceStack> current = builder; current != null; current = current.getChild()) {
            try {
                Integer value = current.build(input).getArgument(ARG_DURATION, Integer.class);
                if (value != null) {
                    return value;
                }
            } catch (IllegalArgumentException ignored) {

            }
        }
        return -1;
    }

    private static int randomBetween(RandomSource random, int min, int max) {
        return min >= max ? min : min + random.nextInt(max - min + 1);
    }
}
