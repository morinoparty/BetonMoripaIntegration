package github.tyonakaisan.betonmoripaintegration.objective.extra;

import edu.umd.cs.findbugs.annotations.NonNull;
import github.tyonakaisan.betonmoripaintegration.util.Checker;
import github.tyonakaisan.betonmoripaintegration.util.InstructionParser;
import github.tyonakaisan.betonmoripaintegration.util.InstructionPrimitiveParser;
import io.papermc.paper.event.player.PlayerTradeEvent;
import io.papermc.paper.registry.RegistryKey;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

/*
    For example:
        tradeObj: "extra:trade test.tradeTestItem amount:10 trader:villager villager:plains,snow profession:cleric,farmer notify events:doneEvent"
 */
@DefaultQualifier(NonNull.class)
public final class TraderTradeObjective extends CountingObjective implements Listener {

    private final QuestItem item;
    private final List<EntityType> traderTypes;
    private final List<Villager.Type> villagerTypes;
    private final List<Villager.Profession> villagerProfessionTypes;
    private final @Nullable String name;
    private final boolean usesProgressForItemAmount;

    public TraderTradeObjective(Instruction instruction) throws InstructionParseException {
        super(instruction, "extra_trade");
        this.targetAmount = instruction.getVarNum(instruction.getOptional("count", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.item = instruction.getQuestItem();
        this.traderTypes = InstructionParser.enumInstruction(TraderType.class)
                .parse(instruction, "traders")
                .map(TraderType::getType)
                .toList();
        this.villagerTypes = InstructionParser.registryInstruction(RegistryKey.VILLAGER_TYPE)
                .parse(instruction, "villagers")
                .toList();
        this.villagerProfessionTypes = InstructionParser.registryInstruction(RegistryKey.VILLAGER_PROFESSION)
                .parse(instruction, "professions")
                .toList();
        this.name = instruction.getOptional("name", null);
        this.usesProgressForItemAmount = InstructionPrimitiveParser.toBoolean(instruction, "uses_progress_for_item_amount", true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTrade(final PlayerTradeEvent event) {
        final var itemStack = event.getTrade().getResult();
        final var trader = event.getVillager();

        // check type and name, item
        if (!this.traderTypes.contains(trader.getType()) && !Checker.name(trader, this.name) && !this.item.compare(itemStack)) {
            return;
        }

        // check villager and wandering trader
        if (!(trader instanceof Villager villager && this.checkVillager(villager)) || !(trader instanceof WanderingTrader)) {
            return;
        }

        final var profile = PlayerConverter.getID(event.getPlayer());
        if (this.containsPlayer(profile) && this.checkConditions(profile)) {
            final var progress = this.usesProgressForItemAmount ? itemStack.getAmount() : 1;
            this.getCountingData(profile).progress(progress);
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

    private boolean checkVillager(final Villager villager) {
        return this.villagerTypes.contains(villager.getVillagerType()) && this.villagerProfessionTypes.contains(villager.getProfession());
    }

    public enum TraderType {
        VILLAGER(EntityType.VILLAGER),
        WANDERING_TRADER(EntityType.WANDERING_TRADER);

        private final EntityType type;

        TraderType(final EntityType type) {
            this.type = type;
        }

        public EntityType getType() {
            return this.type;
        }
    }
}
