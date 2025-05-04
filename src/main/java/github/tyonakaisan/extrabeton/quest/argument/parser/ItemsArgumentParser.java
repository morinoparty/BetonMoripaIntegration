package github.tyonakaisan.extrabeton.quest.argument.parser;

import github.tyonakaisan.extrabeton.quest.argument.ArgumentProperty;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class ItemsArgumentParser extends ArraysArgumentParser<QuestItem> {

    @Override
    public ArgumentProperty<QuestItem> parse(final Instruction instruction, final String key) {
        final var packageID = instruction.getPackage();
        final var values = this.getValues(instruction, key)
                .map(s -> {
                    try {
                        return new QuestItem(new ItemID(packageID, s));
                    } catch (final ObjectNotFoundException | InstructionParseException e) {
                        throw new IllegalArgumentException("Invalid or unknown item id: " + s);
                    }
                })
                .toList();
        return ArgumentProperty.of(values);
    }
}
