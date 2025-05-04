package github.tyonakaisan.extrabeton.quest.objective;

import github.tyonakaisan.extrabeton.quest.argument.ArgumentProperty;
import github.tyonakaisan.extrabeton.quest.argument.parser.EnumArgumentParser;
import github.tyonakaisan.extrabeton.quest.argument.parser.ItemsArgumentParser;
import github.tyonakaisan.extrabeton.quest.argument.parser.PrimitiveArgumentParser;
import github.tyonakaisan.extrabeton.quest.argument.parser.RegistryArgumentParser;
import github.tyonakaisan.extrabeton.util.Checker;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class TraderTradeObjective extends CountingObjective implements Listener {

    private final ArgumentProperty<QuestItem> items;
    private final ArgumentProperty<TraderType> traderTypes;
    private final ArgumentProperty<Villager.Type> villagerTypes;
    private final ArgumentProperty<Villager.Profession> villagerProfessionTypes;
    private final @Nullable String name;
    private final boolean progressForItemAmount;

    public TraderTradeObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "extra_trade");
        this.targetAmount = instruction.getVarNum(instruction.getOptional("amount", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.items = new ItemsArgumentParser()
                .parse(instruction, "items");
        this.traderTypes = new EnumArgumentParser<>(TraderType.class)
                .parse(instruction, "traders");
        this.villagerTypes = new RegistryArgumentParser<>(RegistryKey.VILLAGER_TYPE)
                .parse(instruction, "villagers");
        this.villagerProfessionTypes = new RegistryArgumentParser<>(RegistryKey.VILLAGER_PROFESSION)
                .parse(instruction, "professions");
        this.name = instruction.getOptional("name", null);
        this.progressForItemAmount = PrimitiveArgumentParser.toBoolean(instruction, "progress_for_item_amount", true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTrade(final PlayerTradeEvent event) {
        final var itemStack = event.getTrade().getResult();
        final var trader = event.getVillager();
        final @Nullable TraderType traderType = TraderType.fromBukkitEntityType(trader.getType());

        if (traderType == null) {
            return;
        }

        // check type and name
        if (!this.traderTypes.containsOrEmpty(traderType) && !Checker.name(trader, this.name)) {
            return;
        }

        if (trader instanceof final Villager villager) {
            if (!this.villagerTypes.containsOrEmpty(villager.getVillagerType())) {
                return;
            }

            if (!this.villagerProfessionTypes.containsOrEmpty(villager.getProfession())) {
                return;
            }
        }

        final var profile = PlayerConverter.getID(event.getPlayer());
        if ((this.items.values().isEmpty() || this.items.values().stream().anyMatch(item -> item.compare(event.getTrade().getResult())))
                && this.containsPlayer(profile) && this.checkConditions(profile)) {
            final var progress = this.progressForItemAmount ? itemStack.getAmount() : 1;
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

    public enum TraderType {
        VILLAGER(),
        WANDERING_TRADER();

        TraderType() {
        }

        public static @Nullable TraderType fromBukkitEntityType(final EntityType entityType) {
            return switch (entityType) {
                case VILLAGER -> VILLAGER;
                case WANDERING_TRADER -> WANDERING_TRADER;
                default -> null;
            };
        }
    }
}
