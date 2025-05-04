package github.tyonakaisan.extrabeton.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Nameable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
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
