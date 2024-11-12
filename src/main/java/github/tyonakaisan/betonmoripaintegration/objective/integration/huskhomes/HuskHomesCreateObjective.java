package github.tyonakaisan.betonmoripaintegration.objective.integration.huskhomes;

import net.william278.huskhomes.event.HomeCreateEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class HuskHomesCreateObjective extends Objective implements Listener {

    public HuskHomesCreateObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @EventHandler
    public void onCreate(final HomeCreateEvent event) {
        final var uuid = event.getOwner().getUuid();
        final var profile = PlayerConverter.getID(Bukkit.getOfflinePlayer(uuid));
        if (this.containsPlayer(profile) && this.checkConditions(profile)) {
            this.completeObjective(profile);
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

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
