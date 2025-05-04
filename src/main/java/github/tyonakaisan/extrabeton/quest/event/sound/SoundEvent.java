package github.tyonakaisan.extrabeton.quest.event.sound;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class SoundEvent implements NullableEvent {

    private final Note note;

    public SoundEvent(final Note note) {
        this.note = note;
    }

    @Override
    public void execute(final @Nullable Profile profile) throws QuestRuntimeException {
        if (profile == null) {
            this.worldSound(null);
            return;
        }

        final var onlineProfileOpt = profile.getOnlineProfile();
        if (onlineProfileOpt.isPresent()) {
            this.playerSound(onlineProfileOpt.get());
        } else {
            this.worldSound(profile);
        }
    }

    private void playerSound(final OnlineProfile profile) throws QuestRuntimeException {
        final @Nullable Location location = this.note.location(profile);
        final var player = profile.getPlayer();
        final var sound = this.note.toSound(profile);
        this.playSound(player, sound, location);
    }

    private void worldSound(final @Nullable Profile profile) throws QuestRuntimeException {
        final @Nullable Location location = this.note.location(profile);

        if (location == null) {
            throw new IllegalArgumentException("Location must not be null.");
        }

        final @Nullable World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("World does not exist or is not loaded.");
        }

        this.playSound(world, this.note.toSound(profile), location);
    }

    private void playSound(final Audience audience, final Sound sound, final @Nullable Location location) {
        if (location != null) {
            audience.playSound(sound, location.getX(), location.getY(), location.getZ());
        } else {
            audience.playSound(sound);
        }
    }
}
