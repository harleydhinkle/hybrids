package java.net.wolfking068.hybrids.api.entities.player;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

import java.net.wolfking068.hybrids.api.entities.hybrids.IHybrid;
import java.net.wolfking068.hybrids.api.entities.hybrids.HybridForm;

public interface IHybridPlayer extends IHybrid,IFactionPlayer<IHybridPlayer> {

    boolean isHybrid(); // Always true

    boolean isVampire();

    boolean isWerewolf();

    HybridForm getForm();

    void setForm(HybridForm form);

    void toggleForm(); // Optional toggle behavior
}
