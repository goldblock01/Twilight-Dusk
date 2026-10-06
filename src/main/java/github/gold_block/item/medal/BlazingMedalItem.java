package github.gold_block.item.medal;

import com.google.common.collect.Multimap;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class BlazingMedalItem extends MedalItem {

    private static final String BLAZING_ARMOR = "blazing_armor";
    private static final String BLAZING_TOUGHNESS = "blazing_toughness";

    public BlazingMedalItem(Properties properties) {
        super(properties);
    }

    @Override
    protected Multimap<Attribute, AttributeModifier> modifiers(UUID slotUuid) {
        Multimap<Attribute, AttributeModifier> map = super.modifiers(slotUuid);
        add(map, slotUuid, Attributes.ARMOR, BLAZING_ARMOR, 0.5D);
        add(map, slotUuid, Attributes.ARMOR_TOUGHNESS, BLAZING_TOUGHNESS, 0.5D);
        add(map, slotUuid, Attributes.ATTACK_DAMAGE, DAMAGE, 2.0D);
        return map;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity wearer = slotContext.entity();
        if (wearer.level().isClientSide()) {
            return;
        }
        grantWhileWorn(wearer, MobEffects.FIRE_RESISTANCE);
    }
}
