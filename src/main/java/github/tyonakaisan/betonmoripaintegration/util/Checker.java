package github.tyonakaisan.betonmoripaintegration.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Nameable;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Checker {

    private Checker() {
    }

    public static boolean name(final Nameable nameable, final @Nullable String name) {
        if (name == null) {
            return true;
        }

        final @Nullable Component customName = nameable.customName();
        return customName != null && PlainTextComponentSerializer.plainText().serialize(customName).equals(name);
    }
}
