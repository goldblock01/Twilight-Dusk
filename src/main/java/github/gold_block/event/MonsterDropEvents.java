package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.registry.ModItems;
import github.gold_block.util.EventGuard;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import twilightforest.entity.boss.AlphaYeti;
import twilightforest.entity.monster.CarminiteGolem;
import twilightforest.entity.monster.Yeti;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public class MonsterDropEvents {

    private static final float BASE_CHANCE = 0.1F;
    private static final float LOOTING_CHANCE = 0.03F;
    private static final int ALPHA_YETI_BONES = 18;

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EventGuard.run(() -> {
            LivingEntity victim = event.getEntity();
            if (victim.level().isClientSide()) {
                return;
            }
            if (victim instanceof AlphaYeti) {
                victim.spawnAtLocation(new ItemStack(ModItems.EXTREME_COLD_ICE_BONES.get(), ALPHA_YETI_BONES));
            } else if (victim instanceof Yeti) {
                drop(victim, event, ModItems.EXTREME_COLD_ICE_BONES.get(), 1, 2);
            } else if (victim instanceof CarminiteGolem) {
                drop(victim, event, ModItems.RUSTED_IRON_SHEET.get(), 1, 3);
            }
        });
    }

    private static void drop(LivingEntity victim, LivingDeathEvent event, Item item, int min, int max) {
        RandomSource random = victim.getRandom();
        if (random.nextFloat() >= BASE_CHANCE + looting(event) * LOOTING_CHANCE) {
            return;
        }
        victim.spawnAtLocation(new ItemStack(item, Mth.nextInt(random, min, max)));
    }

    private static int looting(LivingDeathEvent event) {
        return event.getSource().getEntity() instanceof LivingEntity killer ? EnchantmentHelper.getMobLooting(killer) : 0;
    }
}
