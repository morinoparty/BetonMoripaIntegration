package github.tyonakaisan.extrabeton.quest.event.weight;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@DefaultQualifier(NonNull.class)
public final class WeightedRandomEvent implements NullableEvent {

    private final List<WeightedEvent> events;
    private final @Nullable VariableNumber amount;

    public WeightedRandomEvent(final List<WeightedEvent> events, final @Nullable VariableNumber amount) {
        this.events = events;
        this.amount = amount;
    }

    @Override
    public void execute(final @Nullable Profile profile) throws QuestRuntimeException {
        final WeightedRandom<EventID> weightedEventIDs = new WeightedRandom<>();
        for (final WeightedEvent weightedEvent : this.events) {
            weightedEventIDs.add(weightedEvent.eventID(), weightedEvent.weight(profile));
        }

        final List<EventID> selectIDs = new ArrayList<>();
        var count = this.amount != null ? Math.min(weightedEventIDs.size(), this.amount.getValue(profile).intValue()) : 1;
        while (count > 0) {
            final var select = weightedEventIDs.select();
            if (selectIDs.contains(select)) {
                continue;
            }
            selectIDs.add(select);
            count--;
        }
        selectIDs.forEach(selectID -> BetonQuest.event(profile, selectID));
    }

    public record WeightedEvent(EventID eventID, VariableNumber weight) {
        public double weight(final @Nullable Profile profile) throws QuestRuntimeException {
            return this.weight.getValue(profile).doubleValue();
        }
    }
}
