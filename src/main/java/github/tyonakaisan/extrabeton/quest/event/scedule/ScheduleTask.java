package github.tyonakaisan.extrabeton.quest.event.scedule;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public final class ScheduleTask extends BukkitRunnable {

    private final Schedule schedule;
    private final @Nullable Profile profile;

    private final List<ConditionID> conditions;
    private final int totalTicks;
    private int remainingTicks;

    public ScheduleTask(final @Nullable Profile profile, final Schedule schedule) throws QuestRuntimeException {
        this.schedule = schedule;
        this.profile = profile;

        this.conditions = schedule.option().conditions().values();
        this.totalTicks = this.remainingTicks = schedule.option().total(profile);
    }

    @Override
    public void run() {
        if (this.remainingTicks > 0) {
            this.remainingTicks--;
        }

        if (!this.checkConditions()) {
            return;
        }

        try {
            this.schedule.event().fire(this.profile);
        } catch (final QuestRuntimeException e) {
            throw new RuntimeException(e);
        }

        if (this.remainingTicks == 0) {
            this.cancel();
        }
    }

    private boolean checkConditions() {
        if (this.conditions.isEmpty()) {
            return true;
        } else return BetonQuest.conditions(this.profile, this.conditions);
    }
}
