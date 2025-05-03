package github.tyonakaisan.extrabeton.quest.event.scedule;

import github.tyonakaisan.extrabeton.quest.argument.parser.ConditionIDsArgumentParser;
import github.tyonakaisan.extrabeton.quest.argument.parser.EnumArgumentParser;
import github.tyonakaisan.extrabeton.quest.argument.parser.PrimitiveArgumentParser;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.tokenizer.QuotingTokenizer;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.item.typehandler.HandlerUtil;
import org.betonquest.betonquest.quest.event.folder.TimeUnit;
import org.betonquest.betonquest.quest.legacy.LegacyTypeFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

// あんま良くないかも
@DefaultQualifier(NonNull.class)
public final class ScheduleEventFactory implements EventFactory, StaticEventFactory {

    private final BetonQuest betonQuest;
    private final BetonQuestLogger logger;

    public ScheduleEventFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
        this.logger = betonQuest.getLoggerFactory().create(Instruction.class);
    }

    @Override
    public Event parseEvent(final Instruction instruction) {
        return this.createScheduleRunEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) {
        return this.createScheduleRunEvent(instruction);
    }

    private NullableEventAdapter createScheduleRunEvent(final Instruction instruction) {
        final var instructions = this.splitInstructionParts(instruction);
        final var pattern = Pattern.compile("options:(.*?)\\s+event_instruction:(.+)");
        final List<Schedule> result = instructions.stream()
                .map(inst -> this.createSchedule(inst, pattern))
                .filter(Objects::nonNull)
                .toList();
        return new NullableEventAdapter(new ScheduleEvent(result));
    }

    private List<Instruction> splitInstructionParts(final Instruction instruction) {
        final var builder = new StringBuilder();
        final List<Instruction> result = new ArrayList<>();

        for (final String part : instruction.getAllParts()) {
            if (part.startsWith("^")) {
                if (!builder.isEmpty()) {
                    result.add(new Instruction(instruction.getPackage(), instruction.getID(), builder.toString().trim()));
                    builder.setLength(0);
                }
                builder.append(part, 1, part.length());
            } else {
                builder.append(part);
            }
            builder.append(' ');
        }

        if (!builder.isEmpty()) {
            result.add(new Instruction(instruction.getPackage(), instruction.getID(), builder.toString().trim()));
        }

        return result;
    }

    private @Nullable Schedule createSchedule(final Instruction instruction, final Pattern pattern) {
        final var matcher = pattern.matcher(instruction.toString());
        if (matcher.matches()) {
            final ScheduleOption option;
            final QuestEvent event;
            try {
                option = this.createScheduleOption(new Instruction(new QuotingTokenizer(), this.logger, instruction.getPackage(), instruction.getID(), matcher.group(1)));
                event = this.createEvent(matcher.group(2), instruction.getPackage());
            } catch (final InstructionParseException e) {
                throw new RuntimeException(e);
            }
            return new Schedule(option, event);
        } else return null;
    }

    private ScheduleOption createScheduleOption(final Instruction instruction) throws InstructionParseException {
        final var delay = instruction.getVarNum(instruction.getOptional("delay", "0"), VariableNumber.NOT_LESS_THAN_ZERO_CHECKER);
        final var period = instruction.getVarNum(instruction.getOptional("period", "0"), VariableNumber.NOT_LESS_THAN_ZERO_CHECKER);
        final var total = instruction.getVarNum(instruction.getOptional("total", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        final var timeUnit = new EnumArgumentParser<>(TimeUnit.class)
                .parse(instruction, "time_unit")
                .getFirst()
                .orElse(TimeUnit.TICKS);
        final var cancelOnLogout = PrimitiveArgumentParser.toBoolean(instruction, "cancel_on_logout", true);
        final var conditions = new ConditionIDsArgumentParser()
                .parse(instruction, "execute_conditions");

        return new ScheduleOption(delay, period, total, timeUnit, cancelOnLogout, conditions);
    }

    private QuestEvent createEvent(final String string, final QuestPackage questPackage) throws InstructionParseException {
        final String[] parts = HandlerUtil.getNNSplit(string, "Not enough arguments in internal event", " ");
        final @Nullable LegacyTypeFactory<QuestEvent> eventFactory = this.betonQuest.getQuestRegistries().getEventTypes().getFactory(parts[0]);
        if (eventFactory == null) {
            // if it's null then there is no such type registered, log an error
            throw new InstructionParseException("Event type " + parts[0] + " is not registered, check if it's"
                    + " spelled correctly in internal event");
        }
        final Instruction eventInstruction = new Instruction(this.logger, questPackage, null, string);
        return eventFactory.parseInstruction(eventInstruction);
    }
}
