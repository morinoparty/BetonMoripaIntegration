package github.tyonakaisan.extrabeton.quest.event.weight;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class WeightedRandomEventFactory implements EventFactory, StaticEventFactory {

    private final VariableProcessor variableProcessor;

    public WeightedRandomEventFactory(final VariableProcessor variableProcessor) {
        this.variableProcessor = variableProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return this.createPickRandomEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return this.createPickRandomEvent(instruction);
    }

    private NullableEventAdapter createPickRandomEvent(final Instruction instruction) throws InstructionParseException {
        final List<WeightedRandomEvent.WeightedEvent> events = instruction.getList(string -> {
            if (string == null) {
                throw new InstructionParseException("String null.");
            }

            if (!string.matches("(\\d+\\.?\\d?|%.*%):.+")) {
                throw new InstructionParseException("Invalid pattern: " + string);
            }

            final String[] parts = string.split(":");
            EventID eventID;
            try {
                eventID = new EventID(instruction.getPackage(), parts[1]);
            } catch (final ObjectNotFoundException e) {
                throw new InstructionParseException("Error while loading event: " + e.getMessage(), e);
            }
            return new WeightedRandomEvent.WeightedEvent(eventID, new VariableNumber(this.variableProcessor, instruction.getPackage(), parts[0]));
        });
        final @Nullable VariableNumber amount = instruction.getVarNum(instruction.getOptional("amount"));
        return new NullableEventAdapter(new WeightedRandomEvent(events, amount));
    }
}
