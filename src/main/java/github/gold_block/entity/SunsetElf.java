package github.gold_block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

/** 使用悦灵的飞行、声音与自然回血行为，禁用物品交互的夕灵。 */
public class SunsetElf extends Allay {

    /** 在服务端和客户端之间同步外观编号。 */
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(SunsetElf.class, EntityDataSerializers.INT);
    public static final int VARIANT_COUNT = 4;

    public SunsetElf(EntityType<? extends SunsetElf> type, Level level) {
        super(type, level);
        this.entityData.set(DATA_VARIANT, this.random.nextInt(VARIANT_COUNT));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    /** 在可供动物生成的地表方块上方、亮度大于八的位置自然生成。 */
    public static boolean checkSunsetElfSpawnRules(EntityType<SunsetElf> type, LevelAccessor level,
                                                  MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON)
                && level.getRawBrightness(pos, 0) > 8;
    }

    /** 阻止给予、取回物品和紫水晶复制，不建立玩家跟随关系。 */
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    /** 禁止物品传感器选中地面掉落物作为收集目标。 */
    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant());
    }

    /** 保存外观，并将外部 NBT 的编号限制在有效范围内。 */
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Variant", 99)) {
            this.entityData.set(DATA_VARIANT, Math.floorMod(tag.getInt("Variant"), VARIANT_COUNT));
        }
    }
}
