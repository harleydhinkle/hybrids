package java.net.wolfking068.hybrids.api.entities.hybrids;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HybridForm {
    public static final HybridForm VAMPIRE_WEREWOLF = new HybridForm("hybrid");
    public static final HybridForm VAMPIRE_HUNTER   = new HybridForm("dhampir");
    public static final HybridForm WEREWOLF_HUNTER  = new HybridForm("wild hunter");
    @Nonnull
    private final String name;

    // Internal registry
    private static final Map<String, HybridForm> REGISTRY = new HashMap<>();

    // Constructor
    public HybridForm(@Nonnull String name) {
        if (REGISTRY.containsKey(name.toLowerCase())) {
            throw new IllegalStateException("HybridForm with name '" + name + "' already exists.");
        }
        this.name = name.toLowerCase();
        REGISTRY.put(this.name, this);
    }

    /**
     * Get the name of this form.
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Get a form by name.
     */
    @Nullable
    public static HybridForm getForm(@Nonnull String name) {
        return REGISTRY.get(name.toLowerCase());
    }

    /**
     * Get all registered forms.
     */
    @Nonnull
    public static Set<String> getFormNames() {
        return Collections.unmodifiableSet(REGISTRY.keySet());
    }

    @Nonnull
    public static Map<String, HybridForm> getAllForms() {
        return Collections.unmodifiableMap(REGISTRY);
    }
}
