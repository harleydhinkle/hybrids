package net.wolfking068.hybrids.common.skill;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.werewolves.util.Helper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.wolfking068.hybrids.Hybrids;
import net.wolfking068.hybrids.common.player.HybridPlayerData;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = Hybrids.MOD_ID)
public final class HybridSkillPowerHandler {
    private HybridSkillPowerHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        if (!HybridPlayerData.isVampireWerewolf(player) || player.tickCount % 40 != 0) {
            return;
        }

        if (hasEitherSkill(player, ModHybridSkills.VAMPIRE_NIGHT_INSTINCTS.get(), ModHybridSkills.WEREWOLF_NIGHT_INSTINCTS.get())) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, false, false));
            if (Helper.isNight(player.level())) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 0, false, false));
            }
        }

        if (hasEitherSkill(player, ModHybridSkills.VAMPIRE_MOON_FURY.get(), ModHybridSkills.WEREWOLF_MOON_FURY.get())
                && Helper.isFullMoon(player.level())) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 80, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 0, false, false));
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)
                || !HybridPlayerData.isVampireWerewolf(player)
                || player.level().isClientSide) {
            return;
        }

        if (hasEitherSkill(player, ModHybridSkills.VAMPIRE_BLOOD_FRENZY.get(), ModHybridSkills.WEREWOLF_BLOOD_FRENZY.get())) {
            event.setAmount(event.getAmount() * 1.15F);
            player.heal(Math.min(2.0F, event.getAmount() * 0.15F));
        }
    }

    private static boolean hasEitherSkill(Player player, ISkill<?> vampireSkill, ISkill<?> werewolfSkill) {
        return hasSkill(player, vampireSkill) || hasSkill(player, werewolfSkill);
    }

    private static boolean hasSkill(Player player, ISkill<?> skill) {
        Optional<? extends IFactionPlayer<?>> factionPlayer = VampirismAPI.getFactionPlayerHandler(player)
                .resolve()
                .flatMap(IFactionPlayerHandler::getCurrentFactionPlayer);
        return factionPlayer.map(value -> value.getSkillHandler().isSkillEnabled(skill)).orElse(false);
    }
}
