package github.gold_block.item.flask;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record FlaskTooltip(ItemStack flask, FlaskState state) implements TooltipComponent {
}
