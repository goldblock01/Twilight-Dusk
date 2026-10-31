package github.gold_block.network;

import github.gold_block.TwilightDusk;
import github.gold_block.item.KnightSpearItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ModNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(TwilightDusk.MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL)
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .simpleChannel();

    public static void register() {
        CHANNEL.messageBuilder(SpearJabPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SpearJabPacket::encode)
                .decoder(SpearJabPacket::decode)
                .consumerMainThread(SpearJabPacket::handle)
                .add();
    }

    public record SpearJabPacket() {

        public static final SpearJabPacket INSTANCE = new SpearJabPacket();

        public static void encode(SpearJabPacket packet, FriendlyByteBuf buf) {
        }

        public static SpearJabPacket decode(FriendlyByteBuf buf) {
            return INSTANCE;
        }

        public static void handle(SpearJabPacket packet, Supplier<NetworkEvent.Context> context) {
            NetworkEvent.Context ctx = context.get();
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof KnightSpearItem spear && KnightSpearItem.isAttackReady(player)) {
                    spear.performJab(player, stack);
                }
            }
            ctx.setPacketHandled(true);
        }
    }
}
