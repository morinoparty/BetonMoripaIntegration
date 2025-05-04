package github.tyonakaisan.extrabeton.quest.argument.parser;

import org.betonquest.betonquest.Instruction;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@NullMarked
public abstract class ArraysArgumentParser<T> implements InstructionArgumentParser<T> {

    protected Stream<String> getValues(final Instruction instruction, final String key) {
        return Optional.ofNullable(instruction.getOptional(key))
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim))
                .orElseGet(Stream::empty);
    }
}
