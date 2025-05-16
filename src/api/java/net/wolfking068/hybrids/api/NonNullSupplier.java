package net.wolfking068.hybrids.api;
import javax.annotation.Nonnull;
import java.util.function.Supplier;
public interface NonNullSupplier<T> extends Supplier<T> {

    @Nonnull
    @Override
    T get();
}
