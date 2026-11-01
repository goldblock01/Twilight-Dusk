package github.gold_block.event;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import github.gold_block.TwilightDusk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Deserializers;
import github.gold_block.mixin.LootPoolEntriesAccessor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import twilightforest.init.TFItems;
import java.util.Arrays;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 将空药水瓶加入巫妖塔酿造材料箱的普通药水抽选池。 */
@Mod.EventBusSubscriber(modid = TwilightDusk.MODID)
public final class TowerFlaskLootEvents {
    private static final ResourceLocation TOWER_ROOM = new ResourceLocation("twilightforest", "chests/tower_room");
    private static final Gson GSON = Deserializers.createLootTableSerializer().create();

    private TowerFlaskLootEvents() { }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (!TOWER_ROOM.equals(event.getName())) return;
        JsonObject table = GSON.toJsonTree(event.getTable()).getAsJsonObject();
        // 按内容定位药水池，保留表中其他奖励、条件与抽取次数。
        for (var element : table.getAsJsonArray("pools")) {
            JsonArray entries = element.getAsJsonObject().getAsJsonArray("entries");
            int potions = 0;
            boolean sword = false;
            boolean hasFlasks = false;
            for (var entry : entries) {
                JsonObject item = entry.getAsJsonObject();
                if (!item.has("name")) continue;
                String name = item.get("name").getAsString();
                if (name.equals("minecraft:potion")) potions++;
                if (name.equals("minecraft:golden_sword")) sword = true;
                if (name.equals("twilightforest:brittle_flask") || name.equals("twilightforest:greater_flask")) hasFlasks = true;
            }
            if (potions < 4 || !sword) continue;
            if (hasFlasks) return;
            // 空瓶与同池的普通药水分别使用相同权重。
            LootPool pool = event.getTable().getPool(element.getAsJsonObject().get("name").getAsString());
            LootPoolEntriesAccessor access = (LootPoolEntriesAccessor) pool;
            LootPoolEntryContainer[] current = access.twilight_dusk$getEntries();
            LootPoolEntryContainer[] extended = Arrays.copyOf(current, current.length + 2);
            extended[current.length] = LootItem.lootTableItem(TFItems.BRITTLE_FLASK.get()).setWeight(1).build();
            extended[current.length + 1] = LootItem.lootTableItem(TFItems.GREATER_FLASK.get()).setWeight(1).build();
            access.twilight_dusk$setEntries(extended);
            return;
        }
        TwilightDusk.LOGGER.warn("No matching potion pool in {}: flask entries were not added", TOWER_ROOM);
    }

}
