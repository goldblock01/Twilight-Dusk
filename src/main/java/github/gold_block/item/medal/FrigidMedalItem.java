package github.gold_block.item.medal;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FrigidMedalItem extends MedalItem {

    private static final String FRIGID_ARMOR = "frigid_armor";
    private static final String FRIGID_TOUGHNESS = "frigid_toughness";

    public FrigidMedalItem(Properties properties) {
        super(properties);
    }

    @Override
    protected Multimap<Attribute, AttributeModifier> modifiers() {
        Multimap<Attribute, AttributeModifier> map = super.modifiers();
        add(map, Attributes.ARMOR, FRIGID_ARMOR, 0.5D);
        add(map, Attributes.ARMOR_TOUGHNESS, FRIGID_TOUGHNESS, 0.5D);
        return map;
    }
}
