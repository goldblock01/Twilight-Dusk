package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModTags;
import github.gold_block.util.EventGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import twilightforest.TFConfig;
import twilightforest.block.KeepsakeCasketBlock;
import twilightforest.block.entity.KeepsakeCasketBlockEntity;
import twilightforest.entity.CharmEffect;
import twilightforest.enums.BlockLoggingEnum;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFEntities;
import twilightforest.init.TFItems;
import twilightforest.init.TFSounds;
import twilightforest.init.TFStats;
import twilightforest.util.TFItemStackUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public class CasketEvents {

    private static final TagKey<Item> TWILIGHT_KEPT_ON_DEATH = ItemTags.create(new ResourceLocation("twilightforest", "kept_on_death"));
    private static final String STORED_ITEMS = "TwilightDuskKeptItems";
    private static final String STORED_CHARM = "TwilightDuskKeptCharm";

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EventGuard.run(() -> {
            if (event.isCanceled() || !(event.getEntity() instanceof Player player) || player.level().isClientSide()
                    || player instanceof FakePlayer || player.isCreative() || player.isSpectator()
                    || player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                return;
            }
            charmOfKeeping(player);
            keepsakeCasket(player);
        });
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        EventGuard.run(() -> {
            if (event.getEntity() instanceof ServerPlayer player && !event.isEndConquered()) {
                returnStoredItems(player);
            }
        });
    }

    private static void charmOfKeeping(Player player) {
        Inventory kept = new Inventory(null);
        ItemStack charm = consumeCharm(player, kept);
        keepTagged(player.getInventory().items, kept.items);
        keepTagged(player.getInventory().armor, kept.armor);
        keepTagged(player.getInventory().offhand, kept.offhand);
        if (!kept.isEmpty()) {
            store(player, kept.save(new ListTag()), charm);
        }
    }

    private static ItemStack consumeCharm(Player player, Inventory kept) {
        ItemStack charm = keepSlots(TFItems.CHARM_OF_KEEPING_3.get(), player, kept, slot -> true);
        if (charm.isEmpty()) {
            charm = keepSlots(TFItems.CHARM_OF_KEEPING_2.get(), player, kept, Inventory::isHotbarSlot);
        }
        if (charm.isEmpty() && Inventory.isHotbarSlot(player.getInventory().selected)) {
            int selected = player.getInventory().selected;
            charm = keepSlots(TFItems.CHARM_OF_KEEPING_1.get(), player, kept, slot -> slot == selected);
        }
        return charm;
    }

    private static ItemStack keepSlots(Item charm, Player player, Inventory kept, IntPredicate slots) {
        List<ItemStack> affected = new ArrayList<>();
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            if (slots.test(i)) {
                affected.add(player.getInventory().items.get(i));
            }
        }
        affected.addAll(player.getInventory().armor);
        affected.addAll(player.getInventory().offhand);
        if (affected.stream().filter(stack -> !stack.is(charm)).allMatch(ItemStack::isEmpty)) {
            return ItemStack.EMPTY;
        }
        if (!TFItemStackUtils.consumeInventoryItem(player, charm) && !consumeCurio(charm, player)) {
            return ItemStack.EMPTY;
        }

        boolean casketSpared = copySlots(kept.items, player.getInventory().items, slots, charm == TFItems.CHARM_OF_KEEPING_3.get());
        casketSpared = copySlots(kept.armor, player.getInventory().armor, slot -> true, casketSpared);
        copySlots(kept.offhand, player.getInventory().offhand, slot -> true, casketSpared);
        return new ItemStack(charm);
    }

    private static boolean copySlots(NonNullList<ItemStack> to, NonNullList<ItemStack> from, IntPredicate slots, boolean keepCaskets) {
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

    private static void keepsakeCasket(Player player) {
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

        int damage = level.getBlockState(spot).getValue(KeepsakeCasketBlock.BREAKAGE);
        if (level.getRandom().nextFloat() <= 0.15F) {
            if (damage >= 2) {
                ((CasketFinalForm) casket).twilight_dusk$setFinalForm(true);
            } else {
                level.setBlockAndUpdate(spot, state.setValue(KeepsakeCasketBlock.BREAKAGE, damage + 1));
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
        Inventory kept = new Inventory(null);
        kept.load(storedItems(player));
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(TFBlocks.KEEPSAKE_CASKET.get().asItem())) {
                continue;
            }
            kept.add(stack.copy());
            player.getInventory().setItem(i, ItemStack.EMPTY);
        }
        persisted(player).put(STORED_ITEMS, kept.save(new ListTag()));
    }

    private static void returnStoredItems(ServerPlayer player) {
        ListTag items = storedItems(player);
        ItemStack charm = ItemStack.of(persisted(player).getCompound(STORED_CHARM));
        if (items.isEmpty() && charm.isEmpty()) {
            return;
        }

        Inventory stored = new Inventory(null);
        stored.load(items);
        giveBack(player, stored.items, player.getInventory().items);
        giveBack(player, stored.armor, player.getInventory().armor);
        giveBack(player, stored.offhand, player.getInventory().offhand);
        persisted(player).remove(STORED_ITEMS);
        persisted(player).remove(STORED_CHARM);

        if (!charm.isEmpty()) {
            activateCharm(player, charm);
        }
    }

    private static void giveBack(ServerPlayer player, NonNullList<ItemStack> from, NonNullList<ItemStack> to) {
        for (int i = 0; i < from.size(); i++) {
            ItemStack stack = from.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (i < to.size() && to.get(i).isEmpty()) {
                to.set(i, stack);
            } else if (!player.getInventory().add(stack)) {
                player.drop(stack, true);
            }
        }
    }

    private static void activateCharm(ServerPlayer player, ItemStack charm) {
        CharmEffect effect = new CharmEffect(TFEntities.CHARM_EFFECT.get(), player.level(), player, charm.getItem());
        player.level().addFreshEntity(effect);
        CharmEffect mirrored = new CharmEffect(TFEntities.CHARM_EFFECT.get(), player.level(), player, charm.getItem());
        mirrored.offset = (float) Math.PI;
        player.level().addFreshEntity(mirrored);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), TFSounds.CHARM_KEEP.get(), player.getSoundSource(), 1.5F, 1.0F);
        player.awardStat(TFStats.KEEPING_CHARMS_ACTIVATED.get());
    }

    private static void store(Player player, ListTag items, ItemStack charm) {
        CompoundTag data = persisted(player);
        data.put(STORED_ITEMS, items);
        if (!charm.isEmpty()) {
            data.put(STORED_CHARM, charm.save(new CompoundTag()));
        }
    }

    private static ListTag storedItems(Player player) {
        return persisted(player).getList(STORED_ITEMS, 10);
    }

    private static CompoundTag persisted(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(Player.PERSISTED_NBT_TAG)) {
            data.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        return data.getCompound(Player.PERSISTED_NBT_TAG);
    }

    private static boolean consumeCurio(Item charm, Player player) {
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
