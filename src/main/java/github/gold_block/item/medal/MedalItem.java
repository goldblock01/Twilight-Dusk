package github.gold_block.item.medal;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import github.gold_block.TwilightDusk;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;
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
        String prefix = "tooltip." + TwilightDusk.MODID + "." + tooltipId();
        if (Screen.hasShiftDown()) {
            addLines(tooltip, prefix + ".desc");
        } else {
            tooltip.add(Component.literal(Language.getInstance().getOrDefault("tooltip." + TwilightDusk.MODID + ".hold_shift")));
        }
    }

    protected String tooltipId() {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(this);
        return key == null ? "medal" : key.getPath();
    }

    @OnlyIn(Dist.CLIENT)
    private static void addLines(List<Component> tooltip, String key) {
        for (String line : Language.getInstance().getOrDefault(key).split("\n")) {
            tooltip.add(Component.literal(line));
        }
    }
}
