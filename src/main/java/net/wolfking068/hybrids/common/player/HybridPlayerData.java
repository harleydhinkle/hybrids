package net.wolfking068.hybrids.common.player;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.werewolves.api.WReference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.wolfking068.hybrids.Hybrids;
import net.wolfking068.hybrids.api.entities.hybrids.HybridForm;
import net.minecraftforge.common.util.LazyOptional;

public final class HybridPlayerData {
    private static final String PLAYER_PERSISTED_TAG = "PlayerPersisted";
    private static final String FORM_TAG = "Form";
    private static final String FORM_MODE_TAG = "FormMode";
    private static final String ATTEMPTS_TAG = "Attempts";
    private static final String TRANSFORM_TICKS_TAG = "TransformTicks";
    private static final String BITE_ATTEMPTS_TAG = "BiteAttempts";
    private static final String LAST_BITE_TIME_TAG = "LastBiteTime";

    private HybridPlayerData() {
    }

    public static boolean canBecomeVampireWerewolf(Player player) {
        LazyOptional<IFactionPlayerHandler> handler = VampirismAPI.getFactionPlayerHandler(player);
        return handler.map(HybridPlayerData::hasVampireAndWerewolfLevels).orElse(false);
    }

    public static boolean isVampireWerewolf(Player player) {
        return HybridForm.VAMPIRE_WEREWOLF.equals(getForm(player));
    }

    public static HybridForm getForm(Player player) {
        String name = getHybridTag(player).getString(FORM_TAG);
        HybridForm form = HybridForm.getForm(name);
        return form == null ? null : form;
    }

    public static void setForm(Player player, HybridForm form) {
        CompoundTag tag = getHybridTag(player);
        if (form == null) {
            tag.remove(FORM_TAG);
            tag.remove(FORM_MODE_TAG);
            return;
        }
        tag.putString(FORM_TAG, form.getName());
    }

    public static HybridFormMode getFormMode(Player player) {
        return HybridFormMode.byName(getHybridTag(player).getString(FORM_MODE_TAG));
    }

    public static void setFormMode(Player player, HybridFormMode mode) {
        getHybridTag(player).putString(FORM_MODE_TAG, mode.getSerializedName());
    }

    public static int getHybridAttemptCount(Player player) {
        return getHybridTag(player).getInt(ATTEMPTS_TAG);
    }

    public static void setHybridAttemptCount(Player player, int attempts) {
        getHybridTag(player).putInt(ATTEMPTS_TAG, Math.max(0, attempts));
    }

    public static void resetHybridAttempts(Player player) {
        getHybridTag(player).remove(ATTEMPTS_TAG);
    }

    public static int getBiteAttemptCount(Player player) {
        return getHybridTag(player).getInt(BITE_ATTEMPTS_TAG);
    }

    public static void setBiteAttemptCount(Player player, int attempts) {
        getHybridTag(player).putInt(BITE_ATTEMPTS_TAG, Math.max(0, attempts));
    }

    public static void resetBiteAttempts(Player player) {
        getHybridTag(player).remove(BITE_ATTEMPTS_TAG);
    }

    /** Game time of the last bite that counted as a try, so one bite only counts once. */
    public static long getLastBiteTime(Player player) {
        return getHybridTag(player).getLong(LAST_BITE_TIME_TAG);
    }

    public static void setLastBiteTime(Player player, long gameTime) {
        getHybridTag(player).putLong(LAST_BITE_TIME_TAG, gameTime);
    }

    /**
     * Ticks left until a vampire who used a werewolf tooth finishes turning into a hybrid.
     * 0 means they are not turning.
     */
    public static int getTransformTicks(Player player) {
        return getHybridTag(player).getInt(TRANSFORM_TICKS_TAG);
    }

    public static void setTransformTicks(Player player, int ticks) {
        if (ticks <= 0) {
            getHybridTag(player).remove(TRANSFORM_TICKS_TAG);
        } else {
            getHybridTag(player).putInt(TRANSFORM_TICKS_TAG, ticks);
        }
    }

    public static boolean isTransforming(Player player) {
        return getTransformTicks(player) > 0;
    }

    public static void copy(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        CompoundTag oldTag = getHybridTag(oldPlayer);
        if (!oldTag.isEmpty()) {
            CompoundTag persisted = newPlayer.getPersistentData().getCompound(PLAYER_PERSISTED_TAG);
            persisted.put(Hybrids.MOD_ID, oldTag.copy());
            newPlayer.getPersistentData().put(PLAYER_PERSISTED_TAG, persisted);
        }
    }

    private static boolean hasVampireAndWerewolfLevels(IFactionPlayerHandler handler) {
        return handler.getCurrentLevel(VReference.VAMPIRE_FACTION) > 0
                && handler.getCurrentLevel(WReference.WEREWOLF_FACTION) > 0;
    }

    private static CompoundTag getHybridTag(Player player) {
        CompoundTag persisted = player.getPersistentData().getCompound(PLAYER_PERSISTED_TAG);
        player.getPersistentData().put(PLAYER_PERSISTED_TAG, persisted);
        CompoundTag hybridTag = persisted.getCompound(Hybrids.MOD_ID);
        persisted.put(Hybrids.MOD_ID, hybridTag);
        return hybridTag;
    }
}
