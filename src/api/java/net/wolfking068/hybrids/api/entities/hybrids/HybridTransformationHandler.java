package java.net.wolfking068.hybrids.api.entities.hybrids;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.
import net.minecraft.world.damagesource.DamageSource;
import java.net.wolfking068.hybrids.api.entities.hybrids.HybridForm;
import java.net.wolfking068.hybrids.api.entities.player.IHybridPlayer;
import java.net.wolfking068.hybrids.common.player.HybridAPI;
import de.teamlapen.vampirism.api.VampirismAPI;

import de.teamlapen.werewolves.api.entities.player.IWerewolfPlayer;
public class HybridTransformationHandler {
    public static void tryToBecomeHybrid(ServerPlayer player) {
        IHybridPlayer hybrid = HybridAPI.getHybridPlayer(player);
        int attempts = hybrid.getHybridAttemptCount();

        attempts++;
        boolean success = attempts >= 6 || player.getRandom().nextFloat() < 0.2f;

        if (success) {
            hybrid.setForm(HybridForm.VAMPIRE_WEREWOLF);
            hybrid.resetHybridAttempts();

            // Ensure both factions are applied
            if (!VampirismAPI.isVampire(player)) VampirismAPI.makeVampire(player); // Pseudocode
            IWerewolfPlayer wwPlayer = player.getCapability(IWerewolfPlayer.class).orElse(null);
            if (wwPlayer != null && !wwPlayer.isWerewolf()) {
                wwPlayer.setWerewolf(true);
            }

// Pseudocode

            player.sendSystemMessage(Component.literal("You feel something... monstrous awaken inside you."));
        } else {
            hybrid.setHybridAttemptCount(attempts);
            player.sendSystemMessage(Component.literal("The transformation fails... and your body breaks."));
            player.hurt(DamageSource.MAGIC, 10000); // kill them
        }
    }
}
