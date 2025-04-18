package github.tyonakaisan.betonmoripaintegration.extra.argument;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
@DefaultQualifier(NonNull.class)
public record ArgumentProperty<V>(List<V> values) {

    public static <V> ArgumentProperty<V> of(final List<V> values) {
        return new ArgumentProperty<>(values);
    }

    @UnmodifiableView
    public List<V> values() {
        return Collections.unmodifiableList(this.values);
    }

    public boolean containsOrEmpty(final V value) {
        if (this.values.isEmpty()) {
            return true;
        } else return this.values.contains(value);
    }
}
