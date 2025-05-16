package net.wolfking068.hybrids.api;

import javax.annotation.Nonnull;
import java.util.function.Function;
public interface NonNullFunction<T, R> extends Function<T, R> {

    @Nonnull
    @Override
    R apply(T t);
}
