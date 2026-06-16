package net.wolfking068.hybrids.api.entities.hybrids;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.wolfking068.hybrids.common.player.HybridPlayerData;

public class HybridTransformationHandler {
    public static void tryToBecomeHybrid(ServerPlayer player) {
        int attempts = HybridPlayerData.getHybridAttemptCount(player);

        attempts++;
        boolean success = attempts >= 6 || player.getRandom().nextFloat() < 0.2f;

        if (success) {
            HybridPlayerData.setForm(player, HybridForm.VAMPIRE_WEREWOLF);
            HybridPlayerData.resetHybridAttempts(player);

            player.sendSystemMessage(Component.literal("You feel something... monstrous awaken inside you."));
        } else {
            HybridPlayerData.setHybridAttemptCount(player, attempts);
            player.sendSystemMessage(Component.literal("The transformation fails... and your body breaks."));
            player.kill();
        }
    }
}
