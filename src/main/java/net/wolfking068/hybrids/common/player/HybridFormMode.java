package net.wolfking068.hybrids.common.player;

import java.util.Locale;

public enum HybridFormMode {
    HUMAN,
    BEAST,
    FULL;

    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static HybridFormMode byName(String name) {
        for (HybridFormMode mode : values()) {
            if (mode.getSerializedName().equals(name)) {
                return mode;
            }
        }
        return HUMAN;
    }
}
