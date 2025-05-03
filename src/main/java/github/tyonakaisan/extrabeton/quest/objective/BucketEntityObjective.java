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
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class BucketEntityObjective extends CountingObjective implements Listener {

    private final ArgumentProperty<CreatureSpawnEvent.SpawnReason> spawnReasons;
    private final ArgumentProperty<EntityType> entityTypes;

    public BucketEntityObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "extra_bucket");
        this.targetAmount = instruction.getVarNum(instruction.getOptional("amount", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.spawnReasons = new EnumArgumentParser<>(CreatureSpawnEvent.SpawnReason.class)
                .parse(instruction, "spawn_reasons");
        this.entityTypes = new EnumArgumentParser<>(EntityType.class)
                .parse(instruction, "entities");
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucket(final PlayerBucketEntityEvent event) {
        final var type = event.getEntity().getType();
        if (!this.entityTypes.containsOrEmpty(type)) {
            return;
        }

        final var spawnReason = event.getEntity().getEntitySpawnReason();
        if (!this.spawnReasons.containsOrEmpty(spawnReason)) {
            return;
        }

        final var profile = PlayerConverter.getID(event.getPlayer());
        if (this.containsPlayer(profile) && this.checkConditions(profile)) {
            this.getCountingData(profile).progress();
            this.completeIfDoneOrNotify(profile);
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
