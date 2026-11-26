package net.wolfking068.hybrids.common.player;

import de.teamlapen.vampirism.api.event.BloodDrinkEvent;
import de.teamlapen.werewolves.core.ModDamageTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wolfking068.hybrids.Hybrids;
import net.wolfking068.hybrids.api.entities.hybrids.HybridTransformationHandler;

/**
 * Getting bitten is the second way to become a hybrid (8 tries instead of 6):
 * <ul>
 *     <li>A vampire player bitten by a werewolf player (the Werewolves mod's bite attack).</li>
 *     <li>A werewolf player whose blood is drunk by a vampire (a vampire player or a vampire mob).</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = Hybrids.MOD_ID)
public final class HybridBiteEvents {
    private HybridBiteEvents() {
    }

    /** A werewolf bites a vampire. */
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer victim
                && event.getSource().is(ModDamageTypes.BITE)
                && HybridTransformationHandler.isVampire(victim)) {
            HybridTransformationHandler.tryBite(victim);
        }
    }

    /** A vampire player drinks a werewolf's blood. */
    @SubscribeEvent
    public static void onPlayerDrinkBlood(BloodDrinkEvent.PlayerDrinkBloodEvent event) {
        onDrinkBlood(event);
    }

    /** A vampire mob drinks a werewolf's blood. */
    @SubscribeEvent
    public static void onEntityDrinkBlood(BloodDrinkEvent.EntityDrinkBloodEvent event) {
        onDrinkBlood(event);
    }

    private static void onDrinkBlood(BloodDrinkEvent event) {
        event.getBloodSource().getEntity().ifPresent(entity -> {
            if (entity instanceof ServerPlayer victim && HybridTransformationHandler.isWerewolf(victim)) {
                HybridTransformationHandler.tryBite(victim);
            }
        });
    }
}
