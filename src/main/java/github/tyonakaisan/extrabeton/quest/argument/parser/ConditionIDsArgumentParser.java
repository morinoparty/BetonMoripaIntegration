package github.tyonakaisan.extrabeton.quest.argument.parser;

import github.tyonakaisan.extrabeton.quest.argument.ArgumentProperty;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ConditionIDsArgumentParser extends ArraysArgumentParser<ConditionID> {

    @Override
    public ArgumentProperty<ConditionID> parse(final Instruction instruction, final String key) {
        final var packageID = instruction.getPackage();
        final var values = this.getValues(instruction, key)
                .map(s -> {
                    try {
                        return new ConditionID(packageID, s);
                    } catch (final ObjectNotFoundException e) {
                        throw new IllegalArgumentException("Invalid or unknown conditionID: " + s);
                    }
                })
                .toList();
        return ArgumentProperty.of(values);
    }
}
