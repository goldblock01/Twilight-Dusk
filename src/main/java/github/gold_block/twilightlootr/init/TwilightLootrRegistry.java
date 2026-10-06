package github.gold_block.twilightlootr.init;

import github.gold_block.compat.LootrCompat;
import net.minecraftforge.eventbus.api.IEventBus;

public class TwilightLootrRegistry {

    public static void register(IEventBus modEventBus) {
        if (!LootrCompat.isLoaded()) {
            return;
        }
        TLBlocks.BLOCKS.register(modEventBus);
        TLBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        TLItems.ITEMS.register(modEventBus);
    }
}
