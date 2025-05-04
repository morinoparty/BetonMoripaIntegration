package github.tyonakaisan.extrabeton.quest.event.sound;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
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
