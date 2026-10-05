package github.gold_block.item.medal;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import github.gold_block.TwilightDusk;
import github.gold_block.util.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class MedalItem extends Item implements ICurioItem {

    protected static final String ARMOR = "armor";
    protected static final String TOUGHNESS = "toughness";
    protected static final String HEALTH = "health";
    protected static final String DAMAGE = "damage";

    private static final int EFFECT_DURATION = 100;
    private static final int EFFECT_REFRESH = 80;

    public MedalItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    protected Multimap<Attribute, AttributeModifier> modifiers() {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        add(map, Attributes.ARMOR, ARMOR, 1.0D);
        add(map, Attributes.ARMOR_TOUGHNESS, TOUGHNESS, 0.5D);
        return map;
    }

    protected static void add(Multimap<Attribute, AttributeModifier> map, Attribute attribute, String name, double amount) {
        String key = TwilightDusk.MODID + ":medal/" + name;
        UUID uuid = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
        map.put(attribute, new AttributeModifier(uuid, key, amount, AttributeModifier.Operation.ADDITION));
    }

    protected static void grantWhileWorn(LivingEntity wearer, MobEffect effect) {
        MobEffectInstance current = wearer.getEffect(effect);
        if (current == null || current.getDuration() < EFFECT_REFRESH) {
            wearer.addEffect(new MobEffectInstance(effect, EFFECT_DURATION, 0, false, true, true));
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        return modifiers();
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Tooltips.append(this, tooltip);
    }
}
