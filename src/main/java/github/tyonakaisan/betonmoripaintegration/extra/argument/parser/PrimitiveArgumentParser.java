package github.tyonakaisan.betonmoripaintegration.extra.argument.parser;

import org.betonquest.betonquest.Instruction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
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
