package github.gold_block.block;

import github.gold_block.registry.ModEntities;
import github.gold_block.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import twilightforest.init.TFItems;

/** 四阶段失水方块；含水只影响流体状态，阶段仅由喂食推进。 */
public class DriedTowerSpiritBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final SoundType SOUNDS = new SoundType(
            1, 1, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY) {
        @Override public SoundEvent getBreakSound() { return ModSounds.DRIED_BREAK.get(); }
        @Override public SoundEvent getStepSound() { return ModSounds.DRIED_STEP.get(); }
        @Override public SoundEvent getFallSound() { return ModSounds.DRIED_FALL.get(); }
    };
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 10, 13);

    public DriedTowerSpiritBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(STAGE, 0).setValue(WATERLOGGED, false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, STAGE, WATERLOGGED);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                                            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state,
            LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) level.playSound(null, pos, state.getValue(WATERLOGGED)
                ? ModSounds.DRIED_PLACE_IN_WATER.get()
                : ModSounds.DRIED_PLACE.get(), SoundSource.BLOCKS, 1, 1);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        var food = player.getItemInHand(hand);
        if (!food.is(TFItems.CARMINITE.get())) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel server)) return InteractionResult.PASS;
        if (!advance(server, pos, state)) return InteractionResult.FAIL;
        if (!player.getAbilities().instabuild) food.shrink(1);
        level.playSound(null, pos, ModSounds.DRIED_TRANSITION.get(), SoundSource.BLOCKS, 1, 1);
        // 成功喂食时向周围客户端发送绿色成长粒子。
        server.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15, 0.35, 0.35, 0.35, 0.02);
        return InteractionResult.CONSUME;
    }
    /** 最后一次喂食须成功创建幼体后才移除方块；失败时保留材料与原阶段。 */
    public static boolean advance(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.getValue(STAGE) < 3) return level.setBlock(pos, state.setValue(STAGE, state.getValue(STAGE) + 1), 3);
        var baby = ModEntities.TOWER_SPIRIT.get().create(level);
        if (baby == null) return false;
        baby.setAge(-24000);
        baby.moveTo(pos.getX() + 0.5, pos.getY() + 0.625, pos.getZ() + 0.5,
                state.getValue(FACING).toYRot(), 0);
        if (!level.noCollision(baby) || !level.addFreshEntity(baby)) return false;
        baby.playSound(ModSounds.GHASTLING_SPAWN.get(), 1, 1);
        level.setBlock(pos, state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
        return true;
    }
}
