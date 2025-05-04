package github.tyonakaisan.extrabeton.quest.event.sound;

import github.tyonakaisan.extrabeton.quest.argument.parser.EnumArgumentParser;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class SoundEventFactory implements EventFactory, StaticEventFactory {

    private final VariableProcessor variableProcessor;

    public SoundEventFactory(final VariableProcessor variableProcessor) {
        this.variableProcessor = variableProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return this.createSoundEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        return this.createSoundEvent(instruction);
    }

    private NullableEventAdapter createSoundEvent(final Instruction instruction) throws InstructionParseException {
        final var key = Key.key(instruction.getOptional("key", "minecraft:block.stone.break"));
        final var source = new EnumArgumentParser<>(Sound.Source.class)
                .parse(instruction, "source")
                .getFirst()
                .orElse(Sound.Source.MASTER);
        final var volume = instruction.getVarNum(instruction.getOptional("volume", "1"), VariableNumber.NOT_LESS_THAN_ZERO_CHECKER);
        final var pitch = this.createPitch(instruction);
        final @Nullable VariableNumber seed = this.createSeed(instruction);
        final @Nullable VariableLocation location = this.createLocation(instruction);
        return new NullableEventAdapter(new SoundEvent(new Note(key, source, volume, pitch, seed, location)));
    }

    private Pitch createPitch(final Instruction instruction) throws InstructionParseException {
        final var pitch = instruction.getOptional("pitch", "1.0");

        return pitch.matches(SemitoneUtils.PATTERN_REGEX)
                ? new Pitch.Semitone(pitch)
                : new Pitch.Simple(new VariableNumber(this.variableProcessor, instruction.getPackage(), pitch));
    }

    private @Nullable VariableNumber createSeed(final Instruction instruction) throws InstructionParseException {
        final @Nullable String seed = instruction.getOptional("seed");
        return seed != null
                ? instruction.getVarNum(seed)
                : null;
    }

    private @Nullable VariableLocation createLocation(final Instruction instruction) throws InstructionParseException {
        final @Nullable String location = instruction.getOptional("location");
        return location != null
                ? instruction.getLocation(location)
                : null;
    }
}
