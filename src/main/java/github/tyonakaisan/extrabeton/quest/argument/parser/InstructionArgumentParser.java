package github.tyonakaisan.extrabeton.quest.argument.parser;

import github.tyonakaisan.extrabeton.quest.argument.ArgumentProperty;
import org.betonquest.betonquest.Instruction;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface InstructionArgumentParser<T> {

    ArgumentProperty<T> parse(final Instruction instruction, final String key);
}
