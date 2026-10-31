package github.gold_block.item;

import github.gold_block.util.PhantomTier;

public class PhantomKnightSpearItem extends KnightSpearItem implements PhantomEnchantRestriction {

    public PhantomKnightSpearItem(Properties properties) {
        super(PhantomTier.PHANTOM, 1, 0.96F,
                ChargeProfile.of(0.35F, 2.25F, 6.5F, 5.0F, 8.0F, 1.3F),
                3.0F, properties);
    }
}
