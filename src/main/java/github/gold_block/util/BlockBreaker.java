package github.gold_block.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public final class BlockBreaker {

    private BlockBreaker() {
    }

    public static Iterable<BlockPos> area(LivingEntity entity, BlockPos pos, int range) {
        BlockHitResult hitResult = rayTrace(entity, 10.0D, false);
        Direction direction = hitResult.getDirection();
        boolean hasX = direction.getStepX() == 0;
        boolean hasY = direction.getStepY() == 0;
        boolean hasZ = direction.getStepZ() == 0;
        Vec3i start = new Vec3i(hasX ? -range : 0, hasY ? -range : 0, hasZ ? -range : 0);
        Vec3i end = new Vec3i(hasX ? range : 0, hasY ? (range * 2) - 1 : 0, hasZ ? range : 0);
        return BlockPos.betweenClosed(pos.offset(start), pos.offset(end));
    }

    public static BlockHitResult rayTrace(LivingEntity entity, double distance, boolean fluids) {
        HitResult hit = entity.pick(distance, 1.0F, fluids);
        return hit instanceof BlockHitResult blockHit
                ? blockHit
                : BlockHitResult.miss(entity.getEyePosition(), Direction.UP, entity.blockPosition());
    }

    public static boolean breakBlock(Level level, BlockPos pos, ItemStack stack, @Nullable LivingEntity entity) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return false;
        }
        FluidState fluidState = level.getFluidState(pos);
        if (!(state.getBlock() instanceof net.minecraft.world.level.block.BaseFireBlock)) {
            level.levelEvent(2001, pos, Block.getId(state));
        }
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockEntity, entity, stack);
        boolean flag = level.setBlock(pos, fluidState.createLegacyBlock(), 3, 512);
        if (flag) {
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(entity, state));
        }
        return flag;
    }
}
