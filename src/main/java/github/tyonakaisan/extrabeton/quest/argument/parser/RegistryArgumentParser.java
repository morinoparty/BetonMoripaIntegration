package github.tyonakaisan.extrabeton.quest.argument.parser;

import github.tyonakaisan.extrabeton.quest.argument.ArgumentProperty;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.betonquest.betonquest.Instruction;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class RegistryArgumentParser<T extends Keyed> extends ArraysArgumentParser<T> {

    private final RegistryKey<T> registryKey;

    public RegistryArgumentParser(final RegistryKey<T> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public ArgumentProperty<T> parse(final Instruction instruction, final String key) {
        final Registry<@Nullable T> registry = RegistryAccess.registryAccess().getRegistry(this.registryKey);
        final var values = this.getValues(instruction, key)
                .map(s -> {
                    final var key1 = Key.key(s);
                    final @Nullable T value = registry.get(key1);
                    if (value == null) {
                        throw new IllegalArgumentException("Unknown key: " + key1);
                    }
                    return value;
                })
                .toList();
        return ArgumentProperty.of(values);
    }
}
