package github.gold_block.twilightlootr.init;

import net.minecraftforge.eventbus.api.IEventBus;

public class TwilightLootrRegistry {

    public static void register(IEventBus modEventBus) {
        TLBlocks.BLOCKS.register(modEventBus);
        TLBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        TLItems.ITEMS.register(modEventBus);
    }
}
