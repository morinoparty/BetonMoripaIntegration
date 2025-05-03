package github.tyonakaisan.extrabeton.quest.event.scedule;

import org.betonquest.betonquest.api.QuestEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public record Schedule(
        ScheduleOption option,
        QuestEvent event
) {
}
