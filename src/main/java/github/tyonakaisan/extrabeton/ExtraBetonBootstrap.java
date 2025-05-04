package github.tyonakaisan.extrabeton;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@SuppressWarnings({"UnstableApiUsage", "unused"})
@NullMarked
public final class ExtraBetonBootstrap implements PluginBootstrap {

    private @MonotonicNonNull Injector injector;

    @Override
    public void bootstrap(final BootstrapContext context) {
        this.injector = Guice.createInjector(new ExtraBetonBootstrapModule(context));
    }

    @Override
    public @NotNull JavaPlugin createPlugin(final PluginProviderContext context) {
        return new ExtraBeton(this.injector);
    }
}