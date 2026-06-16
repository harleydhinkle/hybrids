package net.wolfking068.hybrids.common.player;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.werewolves.core.ModEffects;
import de.teamlapen.werewolves.core.ModTags;
import net.minecraft.server.level.ServerPlayer;
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
public final class HybridWeaknessHandler {
    private static final int SUN_DAMAGE_INTERVAL = 40;
    private static final float SUN_DAMAGE = 2.0F;
    private static final float SILVER_DAMAGE_MULTIPLIER = 1.5F;
    private static final float VAMPIRE_KILLER_DAMAGE_MULTIPLIER = 1.5F;

    private HybridWeaknessHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level.isClientSide) {
            return;
        }

        Player player = event.player;
        if (!HybridPlayerData.isVampireWerewolf(player)) {
            return;
        }

        applySunWeakness(player);
        applyGarlicWeakness(player);
    }

    @SubscribeEvent
    public static void onHybridHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player) || !HybridPlayerData.isVampireWerewolf(player)) {
            return;
        }

        float amount = event.getAmount();
        if (isSilverAttack(event)) {
            amount *= SILVER_DAMAGE_MULTIPLIER;
            player.addEffect(new MobEffectInstance(ModEffects.SILVER.get(), 100, 0));
        }

        if (Helper.canKillVampires(event.getSource())) {
            amount *= VAMPIRE_KILLER_DAMAGE_MULTIPLIER;
        }

        event.setAmount(amount);
    }

    private static void applySunWeakness(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || serverPlayer.tickCount % SUN_DAMAGE_INTERVAL != 0) {
            return;
        }

        if (Helper.gettingSundamge(player, player.level, player.level.getProfiler())) {
            player.setSecondsOnFire(4);
            player.hurt(VReference.SUNDAMAGE, SUN_DAMAGE);
        }
    }

    private static void applyGarlicWeakness(Player player) {
        EnumStrength garlicStrength = Helper.getGarlicStrength(player, player.level);
        if (garlicStrength == EnumStrength.NONE) {
            return;
        }

        int amplifier = Math.max(0, garlicStrength.getStrength() - 1);
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, amplifier));
    }

    private static boolean isSilverAttack(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return false;
        }

        return isSilverTool(attacker.getMainHandItem()) || isSilverTool(attacker.getOffhandItem());
    }

    private static boolean isSilverTool(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModTags.Items.SILVER_TOOL);
    }
}
