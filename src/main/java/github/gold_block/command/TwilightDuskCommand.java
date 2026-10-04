package github.gold_block.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import github.gold_block.Config;
import github.gold_block.TwilightDusk;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public final class TwilightDuskCommand {

    private static final String ARG_OPTION = "option";
    private static final String ARG_VALUE = "value";

    private TwilightDuskCommand() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(TwilightDusk.MODID)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("config")
                        .executes(context -> list(context.getSource()))
                        .then(Commands.argument(ARG_OPTION, StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    String remaining = builder.getRemainingLowerCase();
                                    Config.EDITABLE_OPTIONS.keySet().stream()
                                            .filter(option -> option.startsWith(remaining))
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(context -> get(context.getSource(),
                                        StringArgumentType.getString(context, ARG_OPTION)))
                                .then(Commands.argument(ARG_VALUE, BoolArgumentType.bool())
                                        .executes(context -> set(context.getSource(),
                                                StringArgumentType.getString(context, ARG_OPTION),
                                                BoolArgumentType.getBool(context, ARG_VALUE)))))));
    }

    private static int list(CommandSourceStack source) {
        int result = requireLoaded(source);
        if (result != 1) {
            return result;
        }
        source.sendSuccess(() -> Component.translatable("commands.twilight_dusk.config.header")
                .withStyle(ChatFormatting.GOLD), false);
        Config.EDITABLE_OPTIONS.forEach((option, value) -> source.sendSuccess(
                () -> Component.translatable("commands.twilight_dusk.config.line", option, color(value.get())), false));
        source.sendSuccess(() -> Component.translatable("commands.twilight_dusk.config.hint")
                .withStyle(ChatFormatting.GRAY), false);
        return Config.EDITABLE_OPTIONS.size();
    }

    private static int get(CommandSourceStack source, String option) {
        int result = requireLoaded(source);
        if (result != 1) {
            return result;
        }
        ForgeConfigSpec.BooleanValue value = Config.EDITABLE_OPTIONS.get(option);
        if (value == null) {
            return unknown(source, option);
        }
        source.sendSuccess(() -> Component.translatable(
                "commands.twilight_dusk.config.line", option, color(value.get())), false);
        return 1;
    }

    private static int set(CommandSourceStack source, String option, boolean newValue) {
        ForgeConfigSpec.BooleanValue value = Config.EDITABLE_OPTIONS.get(option);
        if (value == null) {
            return unknown(source, option);
        }
        int result = requireLoaded(source);
        if (result != 1) {
            return result;
        }

        value.set(newValue);
        Config.SPEC.save();
        source.sendSuccess(() -> Component.translatable(
                "commands.twilight_dusk.config.set", option, color(newValue)).withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static int requireLoaded(CommandSourceStack source) {
        if (Config.isLoaded()) {
            return 1;
        }
        source.sendFailure(Component.translatable("commands.twilight_dusk.config.not_loaded"));
        return 0;
    }

    private static int unknown(CommandSourceStack source, String option) {
        source.sendFailure(Component.translatable("commands.twilight_dusk.config.unknown",
                option, String.join(", ", Config.EDITABLE_OPTIONS.keySet())));
        return 0;
    }

    private static Component color(boolean value) {
        return Component.literal(String.valueOf(value))
                .withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED);
    }
}
