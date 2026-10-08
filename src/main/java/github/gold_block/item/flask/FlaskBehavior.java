package github.gold_block.item.flask;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import twilightforest.init.TFSounds;

import java.util.Optional;

public final class FlaskBehavior {

    private FlaskBehavior() {
    }

    public static boolean stackPotion(ItemStack flask, ItemStack source, Player player, boolean breakable) {
        if (!(source.getItem() instanceof PotionItem)) {
            return false;
        }
        Potion potion = PotionUtils.getPotion(source);
        if (potion == Potions.EMPTY) {
            return false;
        }

        FlaskState state = FlaskState.read(flask, breakable);
        if (!state.accepts(potion) || !state.hasRoom()) {
            return false;
        }

        if (!player.getAbilities().instabuild) {
            source.shrink(1);
            hand(player, new ItemStack(Items.GLASS_BOTTLE));
        }
        state.addDose(potion).write(splitOne(flask, player));
        player.playSound(TFSounds.FLASK_FILL.get(), (state.doses() + 1) * 0.25F,
                player.level().getRandom().nextFloat() * 0.1F + 0.9F);
        return true;
    }

    public static int barWidth(ItemStack stack, boolean breakable) {
        return FlaskState.read(stack, breakable).barWidth();
    }

    public static int maxStackSize(ItemStack stack, boolean breakable) {
        return FlaskState.read(stack, breakable).maxStackSize();
    }

    public static int modelLevel(ItemStack stack) {
        int doses = FlaskState.read(stack, true).doses();
        return doses <= 0 ? 0 : doses + 1;
    }

    public static Optional<TooltipComponent> tooltip(ItemStack stack, boolean breakable) {
        return Optional.of(new FlaskTooltip(stack, FlaskState.read(stack, breakable)));
    }

    private static ItemStack splitOne(ItemStack flask, Player player) {
        if (flask.getCount() <= 1) {
            return flask;
        }
        ItemStack one = flask.copyWithCount(1);
        flask.shrink(1);
        hand(player, one);
        return one;
    }

    private static void hand(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
