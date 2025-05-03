package github.tyonakaisan.extrabeton.quest.argument.parser;

import github.tyonakaisan.extrabeton.quest.argument.ArgumentProperty;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.betonquest.betonquest.Instruction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class EnumArgumentParser<T extends Enum<T>> extends ArraysArgumentParser<T> {

    private final Class<T> clazz;

    public EnumArgumentParser(final Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public ArgumentProperty<T> parse(final Instruction instruction, final String key) {
        final var values = this.getValues(instruction, key)
                .map(s -> {
                    try {
                        return Enum.valueOf(this.clazz, s.toUpperCase());
                    } catch (final IllegalArgumentException e) {
                        ComponentLogger.logger().warn("Invalid enum value: " + s);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        return ArgumentProperty.of(values);
    }
}
