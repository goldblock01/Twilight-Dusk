package github.gold_block.item;

import github.gold_block.util.PhantomTier;

public class PhantomKnightSpearItem extends KnightSpearItem implements PhantomEnchantRestriction {

    public PhantomKnightSpearItem(Properties properties) {
        // 玩家基础 1 + 幻影材质 4 + 参数 3 = 8；对有护甲目标追加 3 点魔法伤害。
        super(PhantomTier.PHANTOM, 3, 3.0F, properties);
    }
}
