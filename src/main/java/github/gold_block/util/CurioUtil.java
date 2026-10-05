package github.gold_block.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;

public class CurioUtil {

    public static boolean isWearing(LivingEntity entity, Item item) {
        return CuriosApi.getCuriosInventory(entity)
                .map(handler -> handler.isEquipped(item))
                .orElse(false);
    }
}
