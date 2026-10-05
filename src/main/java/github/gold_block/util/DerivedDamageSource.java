package github.gold_block.util;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class DerivedDamageSource extends DamageSource {

    public DerivedDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        super(type, directEntity, causingEntity);
    }

    public DerivedDamageSource(DamageSource origin) {
        this(origin.typeHolder(), origin.getDirectEntity(), origin.getEntity());
    }
}
