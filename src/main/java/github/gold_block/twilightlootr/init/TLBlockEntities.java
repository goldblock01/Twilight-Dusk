package github.gold_block.twilightlootr.init;

import github.gold_block.TwilightDusk;
import github.gold_block.twilightlootr.block.entity.TFLootrBossChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TLBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TwilightDusk.MODID);

    public static final RegistryObject<BlockEntityType<TFLootrBossChestBlockEntity>> BOSS_CHEST =
            BLOCK_ENTITIES.register("boss_chest",
                    () -> BlockEntityType.Builder.of(TFLootrBossChestBlockEntity::new, TLBlocks.BOSS_CHEST.get())
                            .build(null));
}
