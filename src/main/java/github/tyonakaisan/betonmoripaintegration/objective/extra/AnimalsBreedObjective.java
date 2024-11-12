package github.tyonakaisan.betonmoripaintegration.objective.extra;

import github.tyonakaisan.betonmoripaintegration.util.InstructionParser;
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

import java.util.List;

/*
    For example:
        extra:breed count:3 entities:cow,pig
 */
@DefaultQualifier(NonNull.class)
public final class AnimalsBreedObjective extends CountingObjective implements Listener {

    private final List<EntityType> entities;

    public AnimalsBreedObjective(Instruction instruction) throws InstructionParseException {
        super(instruction, "extra_breed");
        this.targetAmount = instruction.getVarNum(instruction.getOptional("count", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.entities = InstructionParser.enumInstruction(EntityType.class)
                .parse(instruction, "entities")
                .toList();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreeding(final EntityBreedEvent event) {
        if (event.getBreeder() instanceof Player player) {
            final var type = event.getEntity().getType();
            if (!this.entities.contains(type)) {
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
