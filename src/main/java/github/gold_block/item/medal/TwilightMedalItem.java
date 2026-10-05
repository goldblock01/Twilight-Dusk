package github.gold_block.item.medal;

import com.google.common.collect.Multimap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

public class TwilightMedalItem extends MedalItem {

    private static final ResourceKey<Level> TWILIGHT_FOREST = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation("twilightforest", "twilight_forest"));

    public TwilightMedalItem(Properties properties) {
        super(properties);
    }

    @Override
    protected Multimap<Attribute, AttributeModifier> modifiers() {
        Multimap<Attribute, AttributeModifier> map = super.modifiers();
        add(map, Attributes.MAX_HEALTH, HEALTH, 2.0D);
        return map;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity wearer = slotContext.entity();
        if (wearer.level().isClientSide() || !wearer.level().dimension().equals(TWILIGHT_FOREST)) {
            return;
        }
        grantWhileWorn(wearer, MobEffects.REGENERATION);
    }
}
