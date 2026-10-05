package github.gold_block.block;

import github.gold_block.entity.DuskTadpole;
import github.gold_block.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DuskFrogspawnBlock extends Block {

    private static final int MIN_TADPOLES_SPAWN = 2;
    private static final int MAX_TADPOLES_SPAWN = 5;
    private static final int MIN_HATCH_TICK_DELAY = 3600;
    private static final int MAX_HATCH_TICK_DELAY = 12000;
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.5D, 16.0D);

    public DuskFrogspawnBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return mayPlaceOn(level, pos.below());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, getHatchDelay(level.getRandom()));
    }

    private static int getHatchDelay(RandomSource random) {
        return random.nextInt(MIN_HATCH_TICK_DELAY, MAX_HATCH_TICK_DELAY);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return !this.canSurvive(state, level, pos) ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, false);
        } else {
            this.hatch(level, pos, random);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.getType().equals(EntityType.FALLING_BLOCK)) {
            level.destroyBlock(pos, false);
        }
    }

    private static boolean mayPlaceOn(BlockGetter level, BlockPos pos) {
        FluidState fluid = level.getFluidState(pos);
        FluidState fluidAbove = level.getFluidState(pos.above());
        return fluid.getType() == Fluids.WATER && fluidAbove.getType() == Fluids.EMPTY;
    }

    private void hatch(ServerLevel level, BlockPos pos, RandomSource random) {
        level.destroyBlock(pos, false);
        level.playSound(null, pos, SoundEvents.FROGSPAWN_HATCH, SoundSource.BLOCKS, 1.0F, 1.0F);
        int count = random.nextInt(MIN_TADPOLES_SPAWN, MAX_TADPOLES_SPAWN + 1);
        for (int i = 1; i <= count; i++) {
            DuskTadpole tadpole = ModEntities.DUSK_TADPOLE.get().create(level);
            if (tadpole != null) {
                double x = pos.getX() + this.getRandomOffset(random);
                double z = pos.getZ() + this.getRandomOffset(random);
                int yaw = random.nextInt(1, 361);
                tadpole.moveTo(x, pos.getY() - 0.5D, z, (float) yaw, 0.0F);
                tadpole.setPersistenceRequired();
                level.addFreshEntity(tadpole);
            }
        }
    }

    private double getRandomOffset(RandomSource random) {
        double half = DuskTadpole.HITBOX_WIDTH / 2.0F;
        return Mth.clamp(random.nextDouble(), half, 1.0D - half);
    }
}
