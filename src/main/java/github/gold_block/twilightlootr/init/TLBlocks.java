package github.gold_block.twilightlootr.init;

import github.gold_block.TwilightDusk;
import github.gold_block.twilightlootr.block.TFLootrBossChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TLBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TwilightDusk.MODID);

    public static final RegistryObject<Block> BOSS_CHEST = BLOCKS.register("boss_chest",
            () -> new TFLootrBossChestBlock(BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.5F)));
}
