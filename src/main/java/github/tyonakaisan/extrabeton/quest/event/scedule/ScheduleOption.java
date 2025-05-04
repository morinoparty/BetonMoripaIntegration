package github.tyonakaisan.extrabeton.quest.event.scedule;

import github.tyonakaisan.extrabeton.quest.argument.ArgumentProperty;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.event.folder.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public record ScheduleOption(
        VariableNumber delay,
        VariableNumber period,
        VariableNumber total,
        TimeUnit timeUnit,
        boolean cancelOnLogout,
        ArgumentProperty<ConditionID> conditions
) {

    public long delay(final @Nullable Profile profile) throws QuestRuntimeException {
        return this.delay.getValue(profile).longValue();
    }

    public long period(final @Nullable Profile profile) throws QuestRuntimeException {
        return this.period.getValue(profile).longValue();
    }

    public int total(final @Nullable Profile profile) throws QuestRuntimeException {
        return this.total.getValue(profile).intValue();
    }
}
