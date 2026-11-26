package net.wolfking068.hybrids.api.entities.hybrids;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.werewolves.api.WReference;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.wolfking068.hybrids.Config;
import net.wolfking068.hybrids.common.player.HybridPlayerData;
import net.wolfking068.hybrids.effect.ModEffects;

/**
 * A vampire uses a werewolf tooth, or a werewolf uses a vampire fang, to try to become a
 * vampire-werewolf hybrid. Both work the same way:
 * <ul>
 *     <li>Tries 1 to 5 are a gamble: unlucky = you die, lucky = you turn into a hybrid after 10 minutes.</li>
 *     <li>Try 6 always works, but you turn into a hybrid after 35 minutes.</li>
 * </ul>
 * Getting bitten works the same way, but with 8 tries: a vampire bitten by a werewolf,
 * or a werewolf whose blood is drunk by a vampire.
 * All numbers can be changed in the config (config/hybrids-common.toml).
 */
public class HybridTransformationHandler {
    private static final int TICKS_PER_MINUTE = 20 * 60;

    public static boolean isVampire(Player player) {
        return VampirismAPI.getFactionPlayerHandler(player)
                .map(handler -> handler.isInFaction(VReference.VAMPIRE_FACTION))
                .orElse(false);
    }

    public static boolean isWerewolf(Player player) {
        return VampirismAPI.getFactionPlayerHandler(player)
                .map(handler -> handler.isInFaction(WReference.WEREWOLF_FACTION))
                .orElse(false);
    }

    /** Called when a vampire uses a werewolf tooth, or a werewolf uses a vampire fang. */
    public static void tryToBecomeHybrid(ServerPlayer player) {
        int maxTries = Config.TOOTH_MAX_TRIES.get();
        int attempts = HybridPlayerData.getHybridAttemptCount(player) + 1;
        if (!rollTry(player, attempts, maxTries)) {
            HybridPlayerData.setHybridAttemptCount(player, attempts);
            player.sendSystemMessage(Component.translatable("message.hybrids.tooth.failed", attempts, maxTries));
            die(player);
        }
    }

    /**
     * Called when a vampire is bitten by a werewolf, or a vampire drinks a werewolf's blood.
     * Does nothing if this bite already counted (cooldown), or the player is a hybrid or already turning.
     */
    public static void tryBite(ServerPlayer player) {
        if (HybridPlayerData.isVampireWerewolf(player) || HybridPlayerData.isTransforming(player)) {
            return;
        }
        long now = player.level().getGameTime();
        long cooldownTicks = Config.BITE_COOLDOWN_SECONDS.get() * 20L;
        long lastBite = HybridPlayerData.getLastBiteTime(player);
        if (lastBite > 0 && now - lastBite < cooldownTicks) {
            return;
        }
        HybridPlayerData.setLastBiteTime(player, now);

        int maxTries = Config.BITE_MAX_TRIES.get();
        int attempts = HybridPlayerData.getBiteAttemptCount(player) + 1;
        if (!rollTry(player, attempts, maxTries)) {
            HybridPlayerData.setBiteAttemptCount(player, attempts);
            player.sendSystemMessage(Component.translatable("message.hybrids.bite.failed", attempts, maxTries));
            die(player);
        }
    }

    /** The player dies at the end of this tick (not in the middle of the bite/damage code). */
    private static void die(ServerPlayer player) {
        player.server.tell(new TickTask(player.server.getTickCount(), () -> {
            if (player.isAlive()) {
                player.kill();
            }
        }));
    }

    /**
     * The last try always works. Earlier tries are a gamble.
     * @return true if the transformation started, false if this try failed
     */
    private static boolean rollTry(ServerPlayer player, int attempts, int maxTries) {
        if (attempts >= maxTries) {
            int minutes = Config.GUARANTEED_TRANSFORM_MINUTES.get();
            startTransformation(player, minutes);
            player.sendSystemMessage(Component.translatable("message.hybrids.tooth.guaranteed", minutes));
        } else if (player.getRandom().nextDouble() < Config.TOOTH_LUCKY_CHANCE.get()) {
            int minutes = Config.LUCKY_TRANSFORM_MINUTES.get();
            startTransformation(player, minutes);
            player.sendSystemMessage(Component.translatable("message.hybrids.tooth.lucky", minutes));
        } else {
            return false;
        }
        return true;
    }

    private static void startTransformation(ServerPlayer player, int minutes) {
        HybridPlayerData.resetHybridAttempts(player);
        HybridPlayerData.resetBiteAttempts(player);
        if (minutes <= 0) {
            finishTransformation(player);
            return;
        }
        HybridPlayerData.setTransformTicks(player, minutes * TICKS_PER_MINUTE);
        showTransformationEffect(player, minutes * TICKS_PER_MINUTE);
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 0));
        player.level().playSound(null, player.blockPosition(), SoundEvents.WOLF_GROWL, SoundSource.PLAYERS, 1.0f, 0.8f);
    }

    /** Called every server tick for every player. Counts down the timer. */
    public static void tickTransformation(ServerPlayer player) {
        int ticks = HybridPlayerData.getTransformTicks(player);
        if (ticks <= 0) {
            return;
        }

        if (!isVampire(player) && !isWerewolf(player)) {
            HybridPlayerData.setTransformTicks(player, 0);
            player.removeEffect(ModEffects.HYBRID_TRANSFORMATION.get());
            player.sendSystemMessage(Component.translatable("message.hybrids.transform.cancelled"));
            return;
        }

        ticks--;
        if (ticks <= 0) {
            finishTransformation(player);
            return;
        }
        HybridPlayerData.setTransformTicks(player, ticks);

        // The effect is lost when you die, so put it back with the time that is left
        if (!player.hasEffect(ModEffects.HYBRID_TRANSFORMATION.get())) {
            showTransformationEffect(player, ticks);
        }

        // Once a minute the two bloodlines fight inside your body
        if (ticks % TICKS_PER_MINUTE == 0) {
            if (player.getRandom().nextInt(3) == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0));
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 0));
            }
        }
    }

    /** The potion effect's timer shows how long is left until you become a hybrid. */
    private static void showTransformationEffect(ServerPlayer player, int ticks) {
        player.addEffect(new MobEffectInstance(ModEffects.HYBRID_TRANSFORMATION.get(), ticks, 0, false, false, true));
    }

    private static void finishTransformation(ServerPlayer player) {
        HybridPlayerData.setTransformTicks(player, 0);
        player.removeEffect(ModEffects.HYBRID_TRANSFORMATION.get());
        HybridPlayerData.setForm(player, HybridForm.VAMPIRE_WEREWOLF);
        player.sendSystemMessage(Component.translatable("message.hybrids.transform.done"));
        player.level().playSound(null, player.blockPosition(), SoundEvents.WOLF_HOWL, SoundSource.PLAYERS, 1.5f, 0.7f);
    }

    public static int minutesLeft(int ticks) {
        return (ticks + TICKS_PER_MINUTE - 1) / TICKS_PER_MINUTE;
    }
}
