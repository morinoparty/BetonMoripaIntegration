package github.tyonakaisan.extrabeton.quest.event.scedule;

import org.betonquest.betonquest.api.QuestEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Schedule(
        ScheduleOption option,
        QuestEvent event
) {
}
