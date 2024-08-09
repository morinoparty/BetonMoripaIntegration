package github.tyonakaisan.example;

import com.google.inject.Singleton;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
@Singleton
public final class ExampleProvider {

    private static @Nullable Example instance;

    private ExampleProvider() {

    }

    static void register(final Example instance) {
        ExampleProvider.instance = instance;
    }

    public static Example instance() {
        if (instance == null) {
            throw new IllegalStateException("Example not initialized!");
        }

        return instance;
    }
}