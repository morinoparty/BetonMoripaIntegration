package github.tyonakaisan.extrabeton.quest.event.sound;

import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Map.entry;

@NullMarked
public final class SemitoneUtils {

    public static final String PATTERN_REGEX = "(?i)^([A-G][#b]?)(\\d+)$";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_REGEX);

    private static final int MIN_OCTAVE = 1;
    private static final int MAX_OCTAVE = 3;
    private static final int SEMITONES = 12;

    private static final Map<String, Integer> NOTE_TO_SEMITONES = Map.ofEntries(
            entry("F#", 0), entry("Gb", 0),
            entry("G", 1),
            entry("G#", 2), entry("Ab", 2),
            entry("A", 3),
            entry("A#", 4), entry("Bb", 4),
            entry("B", 5),
            entry("C", 6),
            entry("C#", 7), entry("Db", 7),
            entry("D", 8),
            entry("D#", 9), entry("Eb", 9),
            entry("E", 10),
            entry("F", 11)
    );

    private SemitoneUtils() {
    }

    public static float pitch(final String name) {
        final var matcher = PATTERN.matcher(name.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid pitch name: " + name);
        }

        final var note = matcher.group(1);
        final var octave = Math.clamp(Integer.parseInt(matcher.group(2)), MIN_OCTAVE, MAX_OCTAVE);

        final var semitone = Optional.ofNullable(NOTE_TO_SEMITONES.get(note))
                .orElseThrow(() -> new IllegalArgumentException("Invalid note name: " + note));
        final var offset = semitone + (octave - MIN_OCTAVE) * SEMITONES - SEMITONES;
        return (float) Math.pow(2.0, (double) offset / SEMITONES);
    }
}
