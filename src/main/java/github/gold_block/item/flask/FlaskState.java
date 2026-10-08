package github.gold_block.item.flask;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public record FlaskState(Potion potion, int doses, int breakage, boolean breakable) {

    public static final int CAPACITY = 3;
    public static final int STACK_SIZE = 64;

    private static final String DOSES_TAG = "Uses";
    private static final String BREAKAGE_TAG = "Breakage";

    public static FlaskState read(ItemStack stack, boolean breakable) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return new FlaskState(Potions.EMPTY, 0, 0, breakable);
        }
        return new FlaskState(PotionUtils.getPotion(stack), tag.getInt(DOSES_TAG), tag.getInt(BREAKAGE_TAG), breakable);
    }

    public void write(ItemStack stack) {
        PotionUtils.setPotion(stack, this.potion);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(DOSES_TAG, this.doses);
        tag.putInt(BREAKAGE_TAG, this.breakage);
    }

    public boolean filled() {
        return this.potion != Potions.EMPTY;
    }

    public boolean hasRoom() {
        return this.doses < CAPACITY - this.breakage;
    }

    public boolean accepts(Potion other) {
        return !this.filled() || this.potion == other;
    }

    public FlaskState addDose(Potion potion) {
        return new FlaskState(potion, this.doses + 1, this.breakage, this.breakable);
    }

    public int barWidth() {
        return Math.round(13.0F - Math.abs(this.doses - CAPACITY) * 13.0F / CAPACITY);
    }

    public int maxStackSize() {
        if (!this.breakable) {
            return 1;
        }
        return this.filled() ? 1 : STACK_SIZE;
    }
}
