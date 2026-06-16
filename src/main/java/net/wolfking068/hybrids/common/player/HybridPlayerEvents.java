package net.wolfking068.hybrids.common.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wolfking068.hybrids.Hybrids;

@Mod.EventBusSubscriber(modid = Hybrids.MOD_ID)
public final class HybridPlayerEvents {
    private HybridPlayerEvents() {
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer oldPlayer && event.getEntity() instanceof ServerPlayer newPlayer) {
            HybridPlayerData.copy(oldPlayer, newPlayer);
        }
    }
}
