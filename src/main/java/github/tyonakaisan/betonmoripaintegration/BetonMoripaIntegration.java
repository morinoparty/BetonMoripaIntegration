package github.tyonakaisan.betonmoripaintegration;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import github.tyonakaisan.betonmoripaintegration.integration.griefprevention.GriefPreventionClaimCreateObjective;
import github.tyonakaisan.betonmoripaintegration.integration.huskhomes.HuskHomesCreateObjective;
import github.tyonakaisan.betonmoripaintegration.integration.quickshop.QuickShopCreateObjective;
import github.tyonakaisan.betonmoripaintegration.integration.quickshop.QuickShopSellObjective;
import org.betonquest.betonquest.BetonQuest;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
@Singleton
public final class BetonMoripaIntegration extends JavaPlugin {

    private final Injector injector;

    @Inject
    public BetonMoripaIntegration(
            final Injector bootstrapInjector
    ) {
        this.injector = bootstrapInjector.createChildInjector(new BetonMoripaIntegrationModule(this));
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
}
