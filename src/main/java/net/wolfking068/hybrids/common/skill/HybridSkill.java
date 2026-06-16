package net.wolfking068.hybrids.common.skill;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;

import java.util.Optional;
import java.util.function.Supplier;

public class HybridSkill<T extends IFactionPlayer<T>> extends VampirismSkill<T> {
    private final Supplier<IPlayableFaction<?>> faction;

    public HybridSkill(Supplier<IPlayableFaction<?>> faction) {
        this.faction = faction;
        this.setHasDefaultDescription();
    }

    @Override
    public Optional<IPlayableFaction<?>> getFaction() {
        return Optional.ofNullable(faction.get());
    }
}
