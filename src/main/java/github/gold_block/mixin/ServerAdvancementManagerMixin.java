package github.gold_block.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerMixin {

    private static final ResourceLocation LICH_SCEPTERS = new ResourceLocation("twilightforest", "lich_scepters");

    @Inject(method = "apply", at = @At("HEAD"))
    private void twilightDusk$addBurstScepter(Map<ResourceLocation, JsonElement> advancements, ResourceManager resourceManager,
                                              ProfilerFiller profiler, CallbackInfo ci) {
        JsonElement element = advancements.get(LICH_SCEPTERS);
        if (element == null || !element.isJsonObject()) {
            return;
        }

        JsonObject criteria = child(element.getAsJsonObject(), "criteria");
        JsonObject scepters = criteria == null ? null : child(criteria, "scepters");
        JsonObject conditions = scepters == null ? null : child(scepters, "conditions");
        JsonElement items = conditions == null ? null : conditions.get("items");
        if (items == null || !items.isJsonArray()) {
            return;
        }

        JsonArray names = new JsonArray();
        names.add("twilight_dusk:burst_scepter");
        JsonObject predicate = new JsonObject();
        predicate.add("items", names);
        items.getAsJsonArray().add(predicate);
    }

    private static JsonObject child(JsonObject parent, String key) {
        JsonElement element = parent.get(key);
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }
}
