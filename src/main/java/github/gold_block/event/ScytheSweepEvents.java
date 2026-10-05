package github.gold_block.event;

import github.gold_block.TwilightDusk;
import github.gold_block.item.FieryScytheItem;
import github.gold_block.item.FieryTool;
import github.gold_block.util.EventGuard;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Deque;

@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public final class ScytheSweepEvents {

    private static final ThreadLocal<Deque<Attack>> OPEN_ATTACKS = ThreadLocal.withInitial(ArrayDeque::new);

    private ScytheSweepEvents() {
    }

    public static void beginAttack(Player player, Entity target) {
        OPEN_ATTACKS.get().push(new Attack(player, target));
    }

    public static void endAttack() {
        Deque<Attack> open = OPEN_ATTACKS.get();
        if (!open.isEmpty()) {
            open.pop();
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        EventGuard.run(() -> {
            Attack attack = OPEN_ATTACKS.get().peek();
            if (attack == null) {
                return;
            }
            LivingEntity victim = event.getEntity();

            if (victim == attack.target) {
                return;
            }
            DamageSource source = event.getSource();
            if (source.getEntity() != attack.player) {
                return;
            }
            Player player = attack.player;
            ItemStack weapon = player.getMainHandItem();
            if (!(weapon.getItem() instanceof FieryScytheItem)) {
                return;
            }

            float attackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE)
                    + EnchantmentHelper.getDamageBonus(weapon, victim.getMobType());
            float fraction = FieryScytheItem.SWEEP_DAMAGE_FRACTION
                    + FieryScytheItem.SWEEPING_EDGE_FRACTION
                    * EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, player);
            event.setAmount(attackDamage * fraction);
            FieryTool.burn(victim);
        });
    }

    private record Attack(Player player, Entity target) {
    }
}
