package github.gold_block.event;

import github.gold_block.event.CasketFinalForm;
import github.gold_block.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import top.theillusivec4.curios.api.CuriosApi;
import twilightforest.TFConfig;
import twilightforest.block.KeepsakeCasketBlock;
import twilightforest.block.entity.KeepsakeCasketBlockEntity;
import twilightforest.enums.BlockLoggingEnum;
import twilightforest.events.CharmEvents;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;
import twilightforest.util.TFItemStackUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

public class CasketEvents {

    private static final TagKey<Item> TWILIGHT_KEPT_ON_DEATH = ItemTags.create(new ResourceLocation("twilightforest", "kept_on_death"));

    public static void charmOfKeeping(Player player) {
        Inventory kept = new Inventory(null);

        if (!applyCharm(TFItems.CHARM_OF_KEEPING_3.get(), kept, player, slot -> true)
                && !applyCharm(TFItems.CHARM_OF_KEEPING_2.get(), kept, player, slot -> slot < 9)
                && Inventory.isHotbarSlot(player.getInventory().selected)) {
            int selected = player.getInventory().selected;
            applyCharm(TFItems.CHARM_OF_KEEPING_1.get(), kept, player, slot -> slot == selected);
        }

        keepTagged(player.getInventory().items, kept.items);
        keepTagged(player.getInventory().armor, kept.armor);
        keepTagged(player.getInventory().offhand, kept.offhand);

        if (!kept.isEmpty()) {
            CharmEvents.getPlayerData(player).put(CharmEvents.CHARM_INV_TAG, kept.save(new ListTag()));
        }
    }

    private static boolean applyCharm(Item charm, Inventory kept, Player player, IntPredicate slots) {
        List<ItemStack> affected = new ArrayList<>();
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            if (slots.test(i)) {
                affected.add(player.getInventory().items.get(i));
            }
        }
        affected.addAll(player.getInventory().armor);
        affected.addAll(player.getInventory().offhand);
        if (affected.stream().filter(stack -> !stack.is(charm)).allMatch(ItemStack::isEmpty)) {
            return false;
        }
        if (!TFItemStackUtils.consumeInventoryItem(player, charm) && !hasCharmCurio(charm, player)) {
            return false;
        }

        boolean casketSpared = keepWholeList(kept.items, player.getInventory().items, slots, charm == TFItems.CHARM_OF_KEEPING_3.get());
        casketSpared = keepWholeList(kept.armor, player.getInventory().armor, slot -> true, casketSpared);
        keepWholeList(kept.offhand, player.getInventory().offhand, slot -> true, casketSpared);

        CharmEvents.charmUsed = new ItemStack(charm);
        return true;
    }

    private static boolean keepWholeList(NonNullList<ItemStack> to, NonNullList<ItemStack> from, IntPredicate slots, boolean keepCaskets) {
        boolean casketSpared = keepCaskets;
        for (int i = 0; i < from.size(); i++) {
            if (!slots.test(i)) {
                continue;
            }
            ItemStack stack = from.get(i);
            if (keepCaskets || casketSpared || !stack.is(TFBlocks.KEEPSAKE_CASKET.get().asItem())) {
                to.set(i, stack.copy());
                from.set(i, ItemStack.EMPTY);
            } else {
                casketSpared = true;
                if (stack.getCount() > 1) {
                    to.set(i, stack.copyWithCount(stack.getCount() - 1));
                    from.set(i, stack.copyWithCount(1));
                }
            }
        }
        return casketSpared;
    }

    private static void keepTagged(NonNullList<ItemStack> from, NonNullList<ItemStack> to) {
        for (int i = 0; i < from.size(); i++) {
            ItemStack stack = from.get(i);
            if (stack.is(ModTags.KEPT_ON_DEATH) || stack.is(TWILIGHT_KEPT_ON_DEATH)) {
                to.set(i, stack.copy());
                from.set(i, ItemStack.EMPTY);
            }
        }
    }

    public static void keepsakeCasket(Player player) {
        if (player.getInventory().hasAnyMatching(stack -> !stack.isEmpty() && !stack.is(TFBlocks.KEEPSAKE_CASKET.get().asItem()))) {
            placeCasket(player);
        } else {
            stashCaskets(player);
        }
    }

    private static void placeCasket(Player player) {
        TFItemStackUtils.damage = 0;
        if (!TFItemStackUtils.consumeInventoryItem(player, TFBlocks.KEEPSAKE_CASKET.get().asItem())) {
            return;
        }

        Level level = player.level();
        BlockPos.MutableBlockPos pos = player.blockPosition().mutable();
        if (pos.getY() < level.dimensionType().minY() + 2) {
            pos.setY(level.dimensionType().minY() + 2);
        } else if (pos.getY() > level.dimensionType().logicalHeight()) {
            pos.setY(level.dimensionType().logicalHeight() - 1);
        }
        pos.move(0, -1, 0);
        do {
            pos.move(0, 1, 0);
        } while (!level.getBlockState(pos).canBeReplaced());

        BlockPos spot = pos.immutable();
        FluidState fluid = level.getFluidState(spot);
        BlockState state = TFBlocks.KEEPSAKE_CASKET.get().defaultBlockState()
                .setValue(BlockLoggingEnum.MULTILOGGED, BlockLoggingEnum.getFromFluid(fluid.getType()))
                .setValue(KeepsakeCasketBlock.BREAKAGE, TFItemStackUtils.damage)
                .setValue(KeepsakeCasketBlock.FACING, Direction.from2DDataValue(level.getRandom().nextInt(3)));
        if (!level.setBlockAndUpdate(spot, state)) {
            return;
        }
        if (!(level.getBlockEntity(spot) instanceof KeepsakeCasketBlockEntity casket)) {
            return;
        }

        casket.playeruuid = TFConfig.COMMON_CONFIG.casketUUIDLocking.get() ? player.getUUID() : null;
        String shortName = truncateName(player.getName().getString());
        casket.name = player.getName().getString();
        casket.casketname = shortName;
        casket.setCustomName(Component.literal(shortName + "'s "
                + (level.getRandom().nextInt(1000) == 0 ? "Costco Casket" : casket.getDisplayName().getString())));

        if (level.getRandom().nextFloat() <= 0.15F) {
            if (TFItemStackUtils.damage >= 2) {
                ((CasketFinalForm) casket).twilight_dusk$setFinalForm(true);
            } else {
                level.setBlockAndUpdate(spot, state.setValue(KeepsakeCasketBlock.BREAKAGE, TFItemStackUtils.damage + 1));
            }
        }

        List<ItemStack> contents = new ArrayList<>(casket.getContainerSize());
        contents.addAll(TFItemStackUtils.sortArmorForCasket(player));
        player.getInventory().armor.clear();
        contents.addAll(NonNullList.withSize(4, ItemStack.EMPTY));
        contents.addAll(player.getInventory().offhand);
        player.getInventory().offhand.clear();
        contents.addAll(TFItemStackUtils.sortInvForCasket(player));
        player.getInventory().items.clear();
        casket.setItems(NonNullList.of(ItemStack.EMPTY, contents.toArray(new ItemStack[0])));
    }

    private static void stashCaskets(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(TFBlocks.KEEPSAKE_CASKET.get().asItem())) {
                Inventory kept = new Inventory(null);
                kept.load(CharmEvents.getPlayerData(player).getList(CharmEvents.CHARM_INV_TAG, 10));
                kept.add(player.getInventory().getItem(i).copy());
                player.getInventory().setItem(i, ItemStack.EMPTY);
                CharmEvents.getPlayerData(player).put(CharmEvents.CHARM_INV_TAG, kept.save(new ListTag()));
            }
        }
    }

    private static boolean hasCharmCurio(Item charm, Player player) {
        return CuriosApi.getCuriosHelper().findFirstCurio(player, stack -> stack.is(charm))
                .map(slot -> {
                    slot.stack().shrink(1);
                    return true;
                })
                .orElse(false);
    }

    private static String truncateName(String name) {
        return name.substring(0, Math.min(12, name.length()));
    }
}
