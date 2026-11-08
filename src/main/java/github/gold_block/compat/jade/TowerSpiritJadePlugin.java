package github.gold_block.compat.jade;

import github.gold_block.entity.TowerSpirit;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.Identifiers;
import snownee.jade.api.WailaPlugin;

/** 隐藏高塔乐魂信息框中不适用的自然成长时间。 */
@WailaPlugin
public class TowerSpiritJadePlugin implements IWailaPlugin {
    /** 信息收集完成后，仅移除高塔乐魂的成长时间组件。 */
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addTooltipCollectedCallback((tooltip, accessor) -> {
            if (accessor instanceof EntityAccessor entityAccessor
                    && entityAccessor.getEntity() instanceof TowerSpirit) {
                tooltip.remove(Identifiers.MC_MOB_GROWTH);
            }
        });
    }
}
