package github.tyonakaisan.betonmoripaintegration.extra.argument.parser;

import org.betonquest.betonquest.Instruction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@DefaultQualifier(NonNull.class)
public abstract class ArraysArgumentParser<T> implements InstructionArgumentParser<T> {

    protected Stream<String> getValues(final Instruction instruction, final String key) {
        return Optional.ofNullable(instruction.getOptional(key))
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim))
                .orElseGet(Stream::empty);
    }
}
