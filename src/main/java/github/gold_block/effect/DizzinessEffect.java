package github.gold_block.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;

public class DizzinessEffect extends MobEffect {

    public DizzinessEffect() {
        super(MobEffectCategory.HARMFUL, 0xE6C449);
        // 属性修饰自动乘以效果等级；达到属性下限后不会继续降为负数。
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "de782739-295a-4f54-8a86-25f7a99dbcda",
                -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "a21fb43d-16e0-469f-980b-715f14fdac43",
                -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.FLYING_SPEED, "c98729b3-ddfa-4ea6-8cce-9d3b7c928b56",
                -1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.ATTACK_SPEED, "9d08cc7d-c041-4bad-b9d9-bc230c93242d",
                -0.8D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
