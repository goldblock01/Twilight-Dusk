package github.gold_block.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import github.gold_block.util.BlockBreaker;
import github.gold_block.util.Tooltips;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FieryScytheItem extends SwordItem implements FieryTool {

    private static final int RANGE = 1;

    public static final float SWEEP_DAMAGE_FRACTION = 0.9F;
    public static final float SWEEPING_EDGE_FRACTION = (1.0F - SWEEP_DAMAGE_FRACTION) / 3.0F;

    private static final double SWEEP_RANGE = 1.0D * 1.5D;
    private static final double SWEEP_HEIGHT = 0.25D * 1.5D;

    private static final AttributeModifier ENTITY_REACH_BONUS = new AttributeModifier(
            UUID.fromString("3c6f9e2a-7b41-4f8d-9a17-0d5c8b2e6f34"),
            "Fiery scythe entity reach", 1.0D, AttributeModifier.Operation.ADDITION);

    private static final Map<Block, Block> TILLABLES = Map.of(
            Blocks.GRASS_BLOCK, Blocks.FARMLAND,
            Blocks.DIRT_PATH, Blocks.FARMLAND,
            Blocks.DIRT, Blocks.FARMLAND,
            Blocks.COARSE_DIRT, Blocks.DIRT,
            Blocks.ROOTED_DIRT, Blocks.DIRT);

    public FieryScytheItem(Tier tier, int damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Block result = TILLABLES.get(state.getBlock());
        if (result == null || (!state.is(Blocks.ROOTED_DIRT) && !onlyIfAirAbove(context))) {
            return InteractionResult.PASS;
        }
        Player player = context.getPlayer();
        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide()) {
            if (state.is(Blocks.ROOTED_DIRT)) {
                Block.popResourceFromFace(level, pos, context.getClickedFace(),
                        new ItemStack(Items.HANGING_ROOTS));
            }
            level.setBlock(pos, result.defaultBlockState(), 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
            if (player != null) {
                context.getItemInHand().hurtAndBreak(1, player,
                        e -> e.broadcastBreakEvent(context.getHand()));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static boolean onlyIfAirAbove(UseOnContext context) {
        return context.getClickedFace() != Direction.DOWN
                && context.getLevel().getBlockState(context.getClickedPos().above()).isAir();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result) {
            igniteTarget(target);
        }
        return result;
    }

    private static AABB sweepBox(Entity target) {
        return target.getBoundingBox().inflate(SWEEP_RANGE, SWEEP_HEIGHT, SWEEP_RANGE);
    }

    @Override
    public AABB getSweepHitBox(ItemStack stack, Player player, Entity target) {
        return sweepBox(target);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> base = super.getDefaultAttributeModifiers(slot);
        if (slot != EquipmentSlot.MAINHAND) {
            return base;
        }
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .putAll(base)
                .put(ForgeMod.ENTITY_REACH.get(), ENTITY_REACH_BONUS)
                .build();
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return canMine(state);
    }

    private boolean canMine(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_HOE)
                || state.is(BlockTags.CROPS)
                || state.is(Blocks.COBWEB);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB)) {
            return 15.0F;
        }
        return canMine(state) ? 8.0F : 1.0F;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(canMine(state) ? 1 : 2, entity,
                    e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        if (canMine(state)) {
            for (BlockPos target : BlockBreaker.area(entity, pos, RANGE)) {
                if (level.isClientSide()) {
                    continue;
                }
                BlockState targetState = level.getBlockState(target);
                if (!canMine(targetState)) {
                    continue;
                }
                if (BlockBreaker.breakBlock(level, target, stack, entity)) {
                    if (targetState.getDestroySpeed(level, target) != 0.0F) {
                        stack.hurtAndBreak(1, entity,
                                e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                    }
                }
            }
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Tooltips.append(this, tooltip);
    }
}
