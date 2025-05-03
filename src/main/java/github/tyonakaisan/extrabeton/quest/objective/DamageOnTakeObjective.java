package github.tyonakaisan.extrabeton.quest.objective;

import github.tyonakaisan.extrabeton.quest.argument.ArgumentProperty;
import github.tyonakaisan.extrabeton.quest.argument.parser.EnumArgumentParser;
import github.tyonakaisan.extrabeton.quest.argument.parser.PrimitiveArgumentParser;
import github.tyonakaisan.extrabeton.quest.argument.parser.RegistryArgumentParser;
import io.papermc.paper.registry.RegistryKey;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@SuppressWarnings("UnstableApiUsage")
@DefaultQualifier(NonNull.class)
public final class DamageOnTakeObjective extends CountingObjective implements Listener {

    private final ArgumentProperty<DamageType> damageTypes;
    private final ArgumentProperty<EntityType> entityTypes;
    private final boolean useFinalDamage;

    public DamageOnTakeObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "extra_damage_on_take");
        this.targetAmount = instruction.getVarNum(instruction.getOptional("amount", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.damageTypes = new RegistryArgumentParser<>(RegistryKey.DAMAGE_TYPE)
                .parse(instruction, "damage_types");
        this.entityTypes = new EnumArgumentParser<>(EntityType.class)
                .parse(instruction, "entities");
        this.useFinalDamage = PrimitiveArgumentParser.toBoolean(instruction, "use_final_damage", true);
    }

    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof final Player player) {
            final var source = event.getDamageSource();

            final var damageType = source.getDamageType();
            if (!this.damageTypes.containsOrEmpty(damageType)) {
                return;
            }

            final @Nullable Entity causingEntity = source.getCausingEntity();
            if (causingEntity != null && !this.entityTypes.containsOrEmpty(causingEntity.getType())) {
                return;
            }

            final var profile = PlayerConverter.getID(player);
            if (this.containsPlayer(profile) && this.checkConditions(profile)) {
                final var progress = this.useFinalDamage ? event.getFinalDamage() : event.getDamage();
                this.getCountingData(profile).progress((int) progress);
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
