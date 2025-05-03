package github.tyonakaisan.extrabeton.quest.argument.parser;

import github.tyonakaisan.extrabeton.quest.argument.ArgumentProperty;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConditionID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class ConditionIDsArgumentParser extends ArraysArgumentParser<ConditionID> {

    @Override
    public ArgumentProperty<ConditionID> parse(Instruction instruction, String key) {
        final var packageID = instruction.getPackage();
        final var values = this.getValues(instruction, key)
                .map(s -> {
                    try {
                        return new ConditionID(packageID, s);
                    } catch (final ObjectNotFoundException e) {
                        ComponentLogger.logger().info("Couldn't find condition" + s);
                        return null;
                    }
                })
                .toList();
        return ArgumentProperty.of(values);
    }
}
