package java.net.wolfking068.hybrids.api.entities.hybrids;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
public interface IHybridDataHolder {

    @Nonnull
    HybridForm getForm();
    default int getSkinType() {
        return this.getSkinType(this.getForm());
    }

    default int getEyeType() {
        return this.getEyeType(this.getForm());
    }

    int getSkinType(@Nullable HybridForm form);

    int getEyeType(@Nullable HybridForm form);

    default boolean hasGlowingEyes() {
        return hasGlowingEyes(this.getForm());
    }
    boolean hasGlowingEyes(HybridForm form);


}
