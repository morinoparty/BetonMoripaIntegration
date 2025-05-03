package github.tyonakaisan.extrabeton.extra.argument.parser;

import github.tyonakaisan.extrabeton.extra.argument.ArgumentProperty;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.betonquest.betonquest.Instruction;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class RegistryArgumentParser<T extends Keyed> extends ArraysArgumentParser<T> {

    private final RegistryKey<T> registryKey;

    public RegistryArgumentParser(final RegistryKey<T> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public ArgumentProperty<T> parse(final Instruction instruction, final String key) {
        final var registry = RegistryAccess.registryAccess().getRegistry(this.registryKey);
        final var values = this.getValues(instruction, key)
                .map(s -> {
                    final var namespacedKey = NamespacedKey.minecraft(s);
                    final @Nullable T value = registry.get(namespacedKey);
                    if (value == null) {
                        ComponentLogger.logger().warn("Invalid registry key: " + namespacedKey);
                    }
                    return value;
                })
                .filter(Objects::nonNull)
                .toList();
        return ArgumentProperty.of(values);
    }
}
