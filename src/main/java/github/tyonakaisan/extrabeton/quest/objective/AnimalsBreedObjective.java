package github.tyonakaisan.extrabeton.quest.objective;

import github.tyonakaisan.extrabeton.quest.argument.ArgumentProperty;
import github.tyonakaisan.extrabeton.quest.argument.parser.EnumArgumentParser;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class AnimalsBreedObjective extends CountingObjective implements Listener {

    private final ArgumentProperty<EntityType> entityTypes;

    public AnimalsBreedObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "extra_breed");
        this.targetAmount = instruction.getVarNum(instruction.getOptional("amount", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.entityTypes = new EnumArgumentParser<>(EntityType.class)
                .parse(instruction, "entities");
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreeding(final EntityBreedEvent event) {
        if (event.getBreeder() instanceof final Player player) {
            final var entityType = event.getEntity().getType();
            if (!this.entityTypes.containsOrEmpty(entityType)) {
                return;
            }

            final var profile = PlayerConverter.getID(player);
            if (this.containsPlayer(profile) && this.checkConditions(profile)) {
                this.getCountingData(profile).progress();
                this.completeIfDoneOrNotify(profile);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }
}
