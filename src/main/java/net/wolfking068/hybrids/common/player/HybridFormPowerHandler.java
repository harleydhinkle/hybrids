package net.wolfking068.hybrids.common.player;

import de.teamlapen.werewolves.core.ModEffects;
import de.teamlapen.werewolves.util.Helper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wolfking068.hybrids.Hybrids;

@Mod.EventBusSubscriber(modid = Hybrids.MOD_ID)
public final class HybridFormPowerHandler {
    private static final float BEAST_BITE_BONUS = 2.0F;
    private static final float FULL_BITE_BONUS = 4.0F;

    private HybridFormPowerHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level.isClientSide) {
            return;
        }

        Player player = event.player;
        if (!HybridPlayerData.isVampireWerewolf(player) || player.tickCount % 40 != 0) {
            return;
        }

        HybridFormMode mode = HybridPlayerData.getFormMode(player);
        if (mode == HybridFormMode.BEAST || mode == HybridFormMode.FULL) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, mode == HybridFormMode.FULL ? 1 : 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 80, 0, false, false));
        }

        if (mode == HybridFormMode.FULL) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 80, Helper.isFullMoon(player.level) ? 1 : 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 0, false, false));
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)
                || player.level.isClientSide
                || !HybridPlayerData.isVampireWerewolf(player)
                || !isClawOrBiteAttack(player)) {
            return;
        }

        LivingEntity target = event.getEntity();
        HybridFormMode mode = HybridPlayerData.getFormMode(player);
        if (mode == HybridFormMode.HUMAN) {
            return;
        }

        float bonusDamage = mode == HybridFormMode.FULL ? FULL_BITE_BONUS : BEAST_BITE_BONUS;
        event.setAmount(event.getAmount() + bonusDamage);
        target.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 120, mode == HybridFormMode.FULL ? 1 : 0));
        player.heal(mode == HybridFormMode.FULL ? 2.0F : 1.0F);
    }

    private static boolean isClawOrBiteAttack(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        return mainHand.isEmpty();
    }
}
