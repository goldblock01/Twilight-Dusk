package github.gold_block.item.medal;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class KnightMedalItem extends MedalItem {

    private static final String KNIGHT_ARMOR = "knight_armor";
    private static final String KNIGHT_TOUGHNESS = "knight_toughness";

    public KnightMedalItem(Properties properties) {
        super(properties);
    }

    @Override
    protected Multimap<Attribute, AttributeModifier> modifiers() {
        Multimap<Attribute, AttributeModifier> map = super.modifiers();
        add(map, Attributes.ARMOR, KNIGHT_ARMOR, 2.0D);
        add(map, Attributes.ARMOR_TOUGHNESS, KNIGHT_TOUGHNESS, 1.5D);
        add(map, Attributes.ATTACK_DAMAGE, DAMAGE, 2.0D);
        return map;
    }
}
