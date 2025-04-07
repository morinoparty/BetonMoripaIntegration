package github.tyonakaisan.betonmoripaintegration.extra.argument.parser;

import github.tyonakaisan.betonmoripaintegration.extra.argument.ArgumentProperty;
import org.betonquest.betonquest.Instruction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface InstructionArgumentParser<T> {

    ArgumentProperty<T> parse(final Instruction instruction, final String key);
}
