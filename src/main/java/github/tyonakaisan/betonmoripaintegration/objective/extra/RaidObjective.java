package github.tyonakaisan.betonmoripaintegration.objective.extra;


import github.tyonakaisan.betonmoripaintegration.util.InstructionPrimitiveParser;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Raid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jspecify.annotations.NonNull;

@DefaultQualifier(NonNull.class)
public final class RaidObjective extends CountingObjective implements Listener {

    private final boolean isVictoryOnly;
    private final VariableNumber requiredBadOmenLevel;
    private final VariableNumber requiredPlayers;

    public RaidObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "extra_raid");
        this.targetAmount = instruction.getVarNum(instruction.getOptional("count", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.isVictoryOnly = InstructionPrimitiveParser.toBoolean(instruction, "is_victory_only", true);
        this.requiredBadOmenLevel = instruction.getVarNum(instruction.getOptional("bad_omen_level", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.requiredPlayers = instruction.getVarNum(instruction.getOptional("players", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRaid(final RaidFinishEvent event) {
        final var raid = event.getRaid();

        // check stop reason
        if (this.isVictoryOnly && raid.getStatus() != Raid.RaidStatus.VICTORY) {
            return;
        }

        // huh?
        final var winners = event.getWinners();
        winners.forEach(winner -> {
            try {
                final var profile = PlayerConverter.getID(winner);
                final var badOmenLevel = this.requiredBadOmenLevel.getValue(profile).intValue();
                final var players = this.requiredPlayers.getValue(profile).intValue();

                // check bad omen level & winners
                if (badOmenLevel > raid.getBadOmenLevel() || players > winners.size()) {
                    return;
                }

                if (this.containsPlayer(profile) && this.checkConditions(profile)) {
                    this.getCountingData(profile).progress();
                    this.completeIfDoneOrNotify(profile);
                }
            } catch (final QuestRuntimeException exception) {
                BetonQuest.getInstance().getLoggerFactory().create(RaidObjective.class).error("", exception);
            }
        });
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
