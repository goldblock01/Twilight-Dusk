package github.gold_block.client;

import github.gold_block.item.flask.FlaskState;
import github.gold_block.item.flask.FlaskTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.ArrayList;
import java.util.List;

public class FlaskTooltipComponent implements ClientTooltipComponent {

    private static final int WIDTH = 110;
    private static final int BAR_HEIGHT = 13;
    private static final int BORDER_COLOR = 0xFF3B3B3B;
    private static final int TRACK_COLOR = 0xFF161616;
    private static final int SHADE_COLOR = 0xAA2A2A2A;
    private static final int TEXT_COLOR = 0xFFA0A0A0;
    private static final Component EMPTY_LABEL = Component.translatable("twilight_dusk.flask.empty");
    private static final Component EMPTY_HINT = Component.translatable("twilight_dusk.flask.empty_hint");

    private final ItemStack flask;
    private final FlaskState state;

    public FlaskTooltipComponent(FlaskTooltip tooltip) {
        this.flask = tooltip.flask();
        this.state = tooltip.state();
    }

    @Override
    public int getHeight() {
        Font font = Minecraft.getInstance().font;
        if (!this.state.filled()) {
            return font.lineHeight + 3 + BAR_HEIGHT;
        }
        return 2 + this.wrap(font).size() * font.lineHeight + BAR_HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        return WIDTH;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        int offset;
        if (this.state.filled()) {
            offset = 0;
            for (FormattedCharSequence line : this.wrap(font)) {
                graphics.drawString(font, line, x, y + offset, TEXT_COLOR, false);
                offset += font.lineHeight;
            }
            offset += 2;
        } else {
            graphics.drawString(font, EMPTY_HINT, x, y, TEXT_COLOR, false);
            offset = font.lineHeight + 3;
        }
        this.renderBar(font, graphics, x, y + offset);
    }

    private List<FormattedCharSequence> wrap(Font font) {
        List<FormattedCharSequence> lines = new ArrayList<>();
        for (Component line : this.description()) {
            lines.addAll(font.split(line, WIDTH));
        }
        return lines;
    }

    private List<Component> description() {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable(PotionUtils.getPotion(this.flask).getName("item.minecraft.potion.effect.")));
        PotionUtils.addPotionTooltip(this.flask, lines, 1.0F);
        return lines;
    }

    private void renderBar(Font font, GuiGraphics graphics, int x, int y) {
        int segment = WIDTH / FlaskState.CAPACITY;
        graphics.fill(x, y, x + WIDTH, y + BAR_HEIGHT, BORDER_COLOR);
        graphics.fill(x + 1, y + 1, x + WIDTH - 1, y + BAR_HEIGHT - 1, TRACK_COLOR);

        if (this.state.doses() > 0) {
            graphics.fill(x + 1, y + 1, x + segment * this.state.doses(), y + BAR_HEIGHT - 1,
                    0xFF000000 | PotionUtils.getColor(this.flask));
        }
        if (this.state.breakage() > 0) {
            int from = x + segment * (FlaskState.CAPACITY - this.state.breakage());
            graphics.fill(from, y + 1, x + WIDTH - 1, y + BAR_HEIGHT - 1, SHADE_COLOR);
        }
        for (int i = 1; i < FlaskState.CAPACITY; i++) {
            int split = x + segment * i;
            graphics.fill(split, y + 1, split + 1, y + BAR_HEIGHT - 1, BORDER_COLOR);
        }
        if (this.state.doses() <= 0) {
            graphics.drawCenteredString(font, EMPTY_LABEL, x + WIDTH / 2, y + 3, 0xFFFFFFFF);
        }
    }
}
