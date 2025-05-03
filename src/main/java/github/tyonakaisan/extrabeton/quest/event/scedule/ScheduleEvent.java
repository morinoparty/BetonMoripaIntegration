package github.tyonakaisan.extrabeton.quest.event.scedule;

import github.tyonakaisan.extrabeton.ExtraBeton;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public final class ScheduleEvent implements NullableEvent {

    private final List<Schedule> schedules;

    public ScheduleEvent(
            final List<Schedule> schedules
    ) {
        this.schedules = schedules;
    }

    @Override
    public void execute(final @Nullable Profile profile) {
        final var instance = ExtraBeton.instance();
        this.schedules.forEach(schedule -> {
            try {
                final var option = schedule.option();
                final var delay = option.delay(profile);
                final var period = option.period(profile);

                new ScheduleTask(profile, schedule)
                        .runTaskTimer(instance, delay, period);
            } catch (final QuestRuntimeException e) {
                throw  new RuntimeException(e);
            }
        });
    }
}
