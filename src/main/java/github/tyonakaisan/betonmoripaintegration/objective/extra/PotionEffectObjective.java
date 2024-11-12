package github.tyonakaisan.betonmoripaintegration.objective.extra;

import github.tyonakaisan.betonmoripaintegration.util.InstructionParser;
import github.tyonakaisan.betonmoripaintegration.util.InstructionPrimitiveParser;
import io.papermc.paper.registry.RegistryKey;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public final class PotionEffectObjective extends CountingObjective implements Listener {

    private final List<EntityPotionEffectEvent.Cause> causes;
    private final List<PotionEffectType> potionEffectTypes;
    private final VariableNumber requiredDuration;
    private final VariableNumber requiredAmplifier;
    private final boolean isAmbient;
    private final boolean isVisible;

    public PotionEffectObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction, "extra_effect");
        this.targetAmount = instruction.getVarNum(instruction.getOptional("count", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.causes = InstructionParser.enumInstruction(EntityPotionEffectEvent.Cause.class)
                .parse(instruction, "causes")
                .toList();
        this.potionEffectTypes = InstructionParser.registryInstruction(RegistryKey.MOB_EFFECT)
                .parse(instruction, "effects")
                .toList();
        this.requiredDuration = instruction.getVarNum(instruction.getOptional("duration", "1"), VariableNumber.NOT_LESS_THAN_ONE_CHECKER);
        this.requiredAmplifier = instruction.getVarNum(instruction.getOptional("amplifier", "0"), VariableNumber.NOT_LESS_THAN_ZERO_CHECKER);
        this.isAmbient = InstructionPrimitiveParser.toBoolean(instruction, "is_ambient");
        this.isVisible = InstructionPrimitiveParser.toBoolean(instruction, "is_visible", true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEffect(final EntityPotionEffectEvent event) throws QuestRuntimeException {
        if (event.getEntity() instanceof Player player) {
            final @Nullable PotionEffect effect = event.getNewEffect();
            if (effect == null) {
                return;
            }

            // check cause
            if (!this.causes.contains(event.getCause())) {
                return;
            }

            // check types
            final var type = effect.getType();
            if (!this.potionEffectTypes.contains(type)) {
                return;
            }

            // check some stats
            final var profile = PlayerConverter.getID(player);
            final var duration = this.requiredDuration.getValue(profile).intValue();
            final var amplifier = this.requiredAmplifier.getValue(profile).intValue();
            if (amplifier > effect.getAmplifier() || duration > effect.getDuration()
                    || this.isAmbient != effect.isAmbient() || this.isVisible != (effect.hasIcon() && effect.hasParticles())) { // visible...?
                return;
            }

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
