package github.gold_block.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import github.gold_block.util.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import twilightforest.util.TwilightItemTier;

import java.util.List;
import java.util.UUID;

public class KnightSpearItem extends SwordItem {

    private static final AttributeModifier ENTITY_REACH_BONUS = new AttributeModifier(
            UUID.fromString("10c8f056-7aa1-40cd-b0fb-2c644d1684d2"),
            "Knight spear entity reach", 1.0D, AttributeModifier.Operation.ADDITION);

    private static final AttributeModifier BLOCK_REACH_BONUS = new AttributeModifier(
            UUID.fromString("ebefc160-1dc6-4edf-a930-887e7194fc79"),
            "Knight spear block reach", 1.0D, AttributeModifier.Operation.ADDITION);

    private final float armoredMagicDamage;

    public KnightSpearItem(Properties properties) {
        // 玩家基础 1 + 骑士金属材质 3 + 参数 3 = 7。
        this(TwilightItemTier.KNIGHTMETAL, 3, 2.0F, properties);
    }

    protected KnightSpearItem(Tier tier, int damage, float armoredMagicDamage, Properties properties) {
        // 基础攻速 4 - 2.8 = 1.2；两把矛的耐久均在注册时设为 1240。
        super(tier, damage, -2.8F, properties);
        this.armoredMagicDamage = armoredMagicDamage;
    }

    public float getArmoredMagicDamage() {
        return armoredMagicDamage;
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
                .put(ForgeMod.BLOCK_REACH.get(), BLOCK_REACH_BONUS)
                .build();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Tooltips.append(this, tooltip);
    }
}
