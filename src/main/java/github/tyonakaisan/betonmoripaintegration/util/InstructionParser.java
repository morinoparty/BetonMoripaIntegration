package github.tyonakaisan.betonmoripaintegration.util;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.betonquest.betonquest.Instruction;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@DefaultQualifier(NonNull.class)
public interface InstructionParser<T> {

    Stream<T> parse(final Instruction instruction, final String key);

    static <C extends Enum<C>> EnumArgument<C> enumInstruction(final Class<C> clazz) {
        return new EnumArgument<>(clazz);
    }

    static <C extends Keyed> RegistryArgument<C> registryInstruction(final RegistryKey<C> registryKey) {
        return new RegistryArgument<>(registryKey);
    }

    final class EnumArgument<T extends Enum<T>> implements InstructionParser<T> {

        private final Class<T> clazz;

        EnumArgument(final Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Stream<T> parse(final Instruction instruction, final String key) {
            return Arrays.stream(instruction.getOptionalArgument(key)
                            .map(s -> s.trim().split(","))
                            .orElse(new String[0]))
                    .map(s -> {
                        try {
                            return Enum.valueOf(this.clazz, s.toUpperCase());
                        } catch (final IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull);
        }
    }

    final class RegistryArgument<T extends Keyed> implements InstructionParser<T> {

        private final RegistryKey<T> registryKey;

        RegistryArgument(final RegistryKey<T> registryKey) {
            this.registryKey = registryKey;
        }

        @Override
        public Stream<T> parse(final Instruction instruction, final String key) {
            final var registry = RegistryAccess.registryAccess().getRegistry(this.registryKey);
            return Arrays.stream(instruction.getOptionalArgument(key)
                            .map(s -> s.trim().split(","))
                            .orElse(new String[0]))
                    .map(s -> registry.get(NamespacedKey.minecraft(s)))
                    .filter(Objects::nonNull);
        }
    }
}
