package github.tyonakaisan.extrabeton.quest.argument.parser;

import org.betonquest.betonquest.Instruction;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PrimitiveArgumentParser {

    private PrimitiveArgumentParser() {
    }

    public static boolean toBoolean(final Instruction instruction, final String key) {
        return toBoolean(instruction, key, false);
    }

    public static boolean toBoolean(final Instruction instruction, final String key, final boolean other) {
        return Boolean.parseBoolean(instruction.getOptional(key, Boolean.toString(other)));
    }
}
