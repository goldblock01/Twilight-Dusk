package github.gold_block.event;

import github.gold_block.Config;
import github.gold_block.TwilightDusk;
import github.gold_block.twilightlootr.impl.BossChestHandler;
import github.gold_block.util.EventGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twilightforest.TFConfig;
import twilightforest.entity.EnforcedHomePoint;
import twilightforest.entity.boss.IBossLootBuffer;
import twilightforest.util.EntityUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public class BossChestFallbackEvents {

    private static final int WATCH_TICKS = 600;
    private static final List<Watcher> WATCHERS = new ArrayList<>();

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EventGuard.run(() -> {
            if (!(event.getEntity() instanceof Mob boss) || !(boss instanceof EnforcedHomePoint)
                    || !(boss.level() instanceof ServerLevel level)
                    || !Config.twilightLootrEnabled() || !TFConfig.COMMON_CONFIG.bossDropChests.get()) {
                return;
            }
            BlockPos pos = EntityUtil.bossChestLocation((Mob & EnforcedHomePoint) boss);
            boolean buffered = boss instanceof IBossLootBuffer;
            WATCHERS.add(new Watcher(level, pos, level.getBlockState(pos),
                    buffered ? null : boss.getLootTable(), buffered ? 0L : boss.getLootTableSeed(), WATCH_TICKS));
        });
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || WATCHERS.isEmpty()) {
            return;
        }
        EventGuard.run(() -> {
            Iterator<Watcher> iterator = WATCHERS.iterator();
            while (iterator.hasNext()) {
                Watcher watcher = iterator.next();
                if (BossChestHandler.convertExistingChest(watcher.level, watcher.pos, watcher.before,
                        watcher.lootTable, watcher.seed) || --watcher.ticks <= 0) {
                    iterator.remove();
                }
            }
        });
    }

    private static final class Watcher {

        private final ServerLevel level;
        private final BlockPos pos;
        private final BlockState before;
        private final ResourceLocation lootTable;
        private final long seed;
        private int ticks;

        private Watcher(ServerLevel level, BlockPos pos, BlockState before,
                        ResourceLocation lootTable, long seed, int ticks) {
            this.level = level;
            this.pos = pos;
            this.before = before;
            this.lootTable = lootTable;
            this.seed = seed;
            this.ticks = ticks;
        }
    }
}
