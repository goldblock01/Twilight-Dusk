package github.gold_block.registry;

import github.gold_block.TwilightDusk;
import github.gold_block.block.DuskFrogspawnBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TwilightDusk.MODID);

    public static final RegistryObject<net.minecraft.world.level.block.Block> DUSK_FROGSPAWN =
            BLOCKS.register("dusk_frogspawn", () -> new DuskFrogspawnBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN).instabreak().noOcclusion().noCollission()
                    .sound(SoundType.FROGSPAWN).pushReaction(PushReaction.DESTROY)));
}
