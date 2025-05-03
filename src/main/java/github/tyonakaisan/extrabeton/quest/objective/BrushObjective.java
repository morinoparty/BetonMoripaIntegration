package github.tyonakaisan.extrabeton.quest.objective;

import github.tyonakaisan.extrabeton.quest.argument.ArgumentProperty;
import github.tyonakaisan.extrabeton.quest.argument.parser.ItemsArgumentParser;
import github.tyonakaisan.extrabeton.quest.argument.parser.PrimitiveArgumentParser;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.block.data.Brushable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class BrushObjective extends CountingObjective implements Listener {

    private final ArgumentProperty<QuestItem> items;
    private final boolean usesProgressForItemAmount;

    public BrushObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "extra_brush");
        this.targetAmount = instruction.getVarNum(instruction.getOptional("amount", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.items = new ItemsArgumentParser().parse(instruction, "items");
        this.usesProgressForItemAmount = PrimitiveArgumentParser.toBoolean(instruction, "uses_progress_for_item_amount", true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBrush(final BlockDropItemEvent event) {
        if (event.getBlockState().getBlockData() instanceof Brushable) {
            event.getItems().forEach(item -> {
                final var itemStack = item.getItemStack();
                if (!this.isValidItem(itemStack)) {
                    return;
                }

                final var player = event.getPlayer();
                final var profile = PlayerConverter.getID(player);
                if (this.containsPlayer(profile) && this.checkConditions(profile)) {
                    final var progress = this.usesProgressForItemAmount ? itemStack.getAmount() : 1;
                    this.getCountingData(profile).progress(progress);
                    this.completeIfDoneOrNotify(profile);
                }
            });
        }
    }

    private boolean isValidItem(final ItemStack itemStack) {
        if (this.items.values().isEmpty()) {
            return true;
        } else return this.items.values().stream().anyMatch(questItem -> questItem.compare(itemStack));
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
