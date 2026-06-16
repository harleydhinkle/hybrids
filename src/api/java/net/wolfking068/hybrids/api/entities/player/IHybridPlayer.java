package net.wolfking068.hybrids.api.entities.player;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

import net.wolfking068.hybrids.api.entities.hybrids.IHybrid;
import net.wolfking068.hybrids.api.entities.hybrids.HybridForm;

public interface IHybridPlayer extends IHybrid,IFactionPlayer<IHybridPlayer> {

    boolean isHybrid(); // Always true

    boolean isVampire();

    boolean isWerewolf();

    HybridForm getForm();

    void setForm(HybridForm form);

    void toggleForm(); // Optional toggle behavior

    int getHybridAttemptCount();

    void setHybridAttemptCount(int attempts);

    void resetHybridAttempts();
}
