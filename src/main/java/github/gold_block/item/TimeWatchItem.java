package github.gold_block.item;

import github.gold_block.util.Tooltips;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class TimeWatchItem extends Item implements ICurioItem {

    private static final TagKey<Block> TIME_CORE_EXCLUDED = BlockTags.create(new ResourceLocation("twilightforest", "time_core_excluded"));

    private static final int RANGE = 11;
    private static final int INTERVAL = 20;
    private static final int SAMPLES = 8 * 3 * INTERVAL;

    public TimeWatchItem(Properties properties) {
        super(properties);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity().level() instanceof ServerLevel level) || level.getGameTime() % INTERVAL != 0) {
            return;
        }
        BlockPos origin = slotContext.entity().blockPosition();
        RandomSource random = level.getRandom();
        for (int i = 0; i < SAMPLES; i++) {
            BlockPos pos = origin.offset(random.nextInt(RANGE * 2) - RANGE,
                    random.nextInt(RANGE * 2) - RANGE,
                    random.nextInt(RANGE * 2) - RANGE);
            BlockState state = level.getBlockState(pos);
            if (!state.is(TIME_CORE_EXCLUDED) && state.isRandomlyTicking()) {
                state.randomTick(level, pos, random);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Tooltips.append(this, tooltip);
    }
}
