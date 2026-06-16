package net.wolfking068.hybrids.common.skill;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.werewolves.api.WReference;
import de.teamlapen.werewolves.api.entities.player.IWerewolfPlayer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.wolfking068.hybrids.Hybrids;

public final class ModHybridSkills {
    public static final DeferredRegister<ISkill<?>> SKILLS =
            DeferredRegister.create(VampirismRegistries.SKILLS_ID, Hybrids.MOD_ID);

    public static final RegistryObject<ISkill<IVampirePlayer>> VAMPIRE_NIGHT_INSTINCTS =
            SKILLS.register("vampire_night_instincts", () -> new HybridSkill<>(() -> VReference.VAMPIRE_FACTION));
    public static final RegistryObject<ISkill<IVampirePlayer>> VAMPIRE_MOON_FURY =
            SKILLS.register("vampire_moon_fury", () -> new HybridSkill<>(() -> VReference.VAMPIRE_FACTION));
    public static final RegistryObject<ISkill<IVampirePlayer>> VAMPIRE_BLOOD_FRENZY =
            SKILLS.register("vampire_blood_frenzy", () -> new HybridSkill<>(() -> VReference.VAMPIRE_FACTION));

    public static final RegistryObject<ISkill<IWerewolfPlayer>> WEREWOLF_NIGHT_INSTINCTS =
            SKILLS.register("werewolf_night_instincts", () -> new HybridSkill<>(() -> WReference.WEREWOLF_FACTION));
    public static final RegistryObject<ISkill<IWerewolfPlayer>> WEREWOLF_MOON_FURY =
            SKILLS.register("werewolf_moon_fury", () -> new HybridSkill<>(() -> WReference.WEREWOLF_FACTION));
    public static final RegistryObject<ISkill<IWerewolfPlayer>> WEREWOLF_BLOOD_FRENZY =
            SKILLS.register("werewolf_blood_frenzy", () -> new HybridSkill<>(() -> WReference.WEREWOLF_FACTION));

    private ModHybridSkills() {
    }

    public static void register(IEventBus eventBus) {
        SKILLS.register(eventBus);
    }
}
