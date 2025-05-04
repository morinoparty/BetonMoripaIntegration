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
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@NullMarked
public final class DamageObjective extends CountingObjective implements Listener {

    private final TriggerType triggerType;
    private final ArgumentProperty<DamageType> damageTypes;
    private final ArgumentProperty<EntityType> entityTypes;
    private final boolean useFinalDamage;
    private final boolean damageCap;

    public DamageObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "extra_damage");
        this.targetAmount = instruction.getVarNum(instruction.getOptional("amount", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.triggerType = new EnumArgumentParser<>(TriggerType.class)
                .parse(instruction, "trigger_type")
                .getFirst()
                .orElse(TriggerType.TAKE);
        this.damageTypes = new RegistryArgumentParser<>(RegistryKey.DAMAGE_TYPE)
                .parse(instruction, "damage_types");
        this.entityTypes = new EnumArgumentParser<>(EntityType.class)
                .parse(instruction, "entities");
        this.useFinalDamage = PrimitiveArgumentParser.toBoolean(instruction, "use_final_damage", true);
        this.damageCap = PrimitiveArgumentParser.toBoolean(instruction, "damage_cap", false);
    }

    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (this.triggerType == TriggerType.TAKE) {
            this.take(event);
        } else {
            this.attack(event);
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

    private void take(final EntityDamageEvent event) {
        if (event.getEntity() instanceof final Player player) {
            final var source = event.getDamageSource();

            final var damageType = source.getDamageType();
            if (!this.damageTypes.containsOrEmpty(damageType)) {
                return;
            }

            final @Nullable Entity entity = source.getCausingEntity();
            if (entity != null && !this.entityTypes.containsOrEmpty(entity.getType())) {
                return;
            }

            final var profile = PlayerConverter.getID(player);
            if (this.containsPlayer(profile) && this.checkConditions(profile)) {
                final var damage = this.useFinalDamage ? event.getFinalDamage() : event.getDamage();
                this.getCountingData(profile).progress(this.progress((int) damage, entity));
                this.completeIfDoneOrNotify(profile);
            }
        }
    }

    private void attack(final EntityDamageEvent event) {
        final var source = event.getDamageSource();
        if (source.getCausingEntity() instanceof final Player player) {
            final var damageType = source.getDamageType();
            if (!this.damageTypes.containsOrEmpty(damageType)) {
                return;
            }

            final var entity = event.getEntity();
            if (!this.entityTypes.containsOrEmpty(entity.getType())) {
                return;
            }

            final var profile = PlayerConverter.getID(player);
            if (this.containsPlayer(profile) && this.checkConditions(profile)) {
                final var damage = this.useFinalDamage ? event.getFinalDamage() : event.getDamage();
                this.getCountingData(profile).progress(this.progress((int) damage, entity));
                this.completeIfDoneOrNotify(profile);
            }
        }
    }

    private int progress(final int damage, final @Nullable Entity entity) {
        if (entity instanceof final LivingEntity livingEntity) {
            if (this.damageCap) {
                final @Nullable AttributeInstance attribute = livingEntity.getAttribute(Attribute.MAX_HEALTH);
                final var max = attribute != null ? attribute.getValue() : Integer.MAX_VALUE;
                return (int) Math.min(damage, max);
            } else return damage;
        } else return damage;
    }

    public enum TriggerType {
        TAKE,
        ATTACK
    }
}
