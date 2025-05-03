package github.tyonakaisan.extrabeton;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import github.tyonakaisan.extrabeton.extra.event.scedule.ScheduleEventFactory;
import github.tyonakaisan.extrabeton.extra.event.weight.WeightedRandomEventFactory;
import github.tyonakaisan.extrabeton.extra.objective.*;
import github.tyonakaisan.extrabeton.integration.griefprevention.GriefPreventionClaimCreateObjective;
import github.tyonakaisan.extrabeton.integration.huskhomes.HuskHomesCreateObjective;
import github.tyonakaisan.extrabeton.integration.quickshop.QuickShopCreateObjective;
import github.tyonakaisan.extrabeton.integration.quickshop.QuickShopSellObjective;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.betonquest.betonquest.BetonQuest;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
@Singleton
public final class ExtraBeton extends JavaPlugin {

    private final Injector injector;
    private static @Nullable ExtraBeton instance;

    @Inject
    public ExtraBeton(
            final Injector bootstrapInjector
    ) {
        this.injector = bootstrapInjector.createChildInjector(new ExtraBetonModule(this));

        instance = this;
    }

    @Override
    public void onEnable() {
        final var betonQuest = BetonQuest.getInstance();
        if (griefPreventionLoaded()) {
            betonQuest.registerObjectives("claim", GriefPreventionClaimCreateObjective.class);
        }

        if (huskHomesLoaded()) {
            betonQuest.registerObjectives("home", HuskHomesCreateObjective.class);
        }

        if (quickShopLoaded()) {
            betonQuest.registerObjectives("qssell", QuickShopSellObjective.class);
            betonQuest.registerObjectives("qscreate", QuickShopCreateObjective.class);
        }

        // experimental
        // objective
        betonQuest.registerObjectives("extra:trade", TraderTradeObjective.class);
        betonQuest.registerObjectives("extra:bucket", BucketEntityObjective.class);
        betonQuest.registerObjectives("extra:breed", AnimalsBreedObjective.class);
        betonQuest.registerObjectives("extra:raid", RaidObjective.class);
        betonQuest.registerObjectives("extra:effect", PotionEffectObjective.class);
        betonQuest.registerObjectives("extra:damage_on_take", DamageOnTakeObjective.class);
        betonQuest.registerObjectives("extra:brush", BrushObjective.class);
        // event
        final var variableProcessor = betonQuest.getVariableProcessor();
        betonQuest.getQuestRegistries().getEventTypes().registerCombined("weight", new WeightedRandomEventFactory(variableProcessor));
        betonQuest.getQuestRegistries().getEventTypes().registerCombined("run_schedule", new ScheduleEventFactory(betonQuest));

        this.getComponentLogger().info(MiniMessage.miniMessage().deserialize("""
                <yellow>Experimental features are enabled.
                Not all features have been fully debugged and should be used at your own risk.
                Experimental features are subject to change or removal without notice."""));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static boolean griefPreventionLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled("GriefPrevention");
    }

    public static boolean huskHomesLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled("HuskHomes");
    }

    public static boolean quickShopLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled("QuickShop-Hikari");
    }

    public static ExtraBeton instance() {
        if (instance == null) {
            throw new IllegalStateException("ExtraBeton not initialized!");
        } else return instance;
    }
}
