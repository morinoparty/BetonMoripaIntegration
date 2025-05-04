package github.tyonakaisan.extrabeton.quest.event.sound;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface Pitch {

    float value(final @Nullable Profile profile) throws QuestRuntimeException;

    record Simple(VariableNumber pitch) implements Pitch {

        @Override
        public float value(final @Nullable Profile profile) throws QuestRuntimeException {
            return this.pitch.getValue(profile).floatValue();
        }
    }

    record Semitone(String name) implements Pitch {

        @Override
        public float value(final @Nullable Profile profile) {
            return SemitoneUtils.pitch(this.name.toUpperCase());
        }
    }
}
