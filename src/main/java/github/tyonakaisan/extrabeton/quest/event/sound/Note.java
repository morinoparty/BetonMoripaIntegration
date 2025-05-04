package github.tyonakaisan.extrabeton.quest.event.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public record Note(
        Key key,
        Sound.Source source,
        VariableNumber volume,
        Pitch pitch,
        @Nullable VariableNumber seed,
        @Nullable VariableLocation location
) {
    public float volume(final @Nullable Profile profile) throws QuestRuntimeException {
        return this.volume.getValue(profile).floatValue();
    }

    public @Nullable Long seed(final @Nullable Profile profile) throws QuestRuntimeException {
        if (this.seed != null) {
            return this.seed.getValue(profile).longValue();
        } else return null;
    }

    public @Nullable Location location(final @Nullable Profile profile) throws QuestRuntimeException {
        if (this.location != null) {
            return this.location.getValue(profile);
        } else return null;
    }

    public Sound toSound(final @Nullable Profile profile) throws QuestRuntimeException {
        final var builder = Sound.sound();

        builder.type(this.key);
        builder.source(this.source);
        builder.volume(this.volume(profile));
        builder.pitch(this.pitch.value(profile));

        final @Nullable Long seed = this.seed(profile);
        if (seed != null) {
            builder.seed(seed);
        }

        return builder.build();
    }
}
