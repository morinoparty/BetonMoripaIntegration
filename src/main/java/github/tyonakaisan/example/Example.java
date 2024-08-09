package github.tyonakaisan.example;

import com.google.inject.*;
import github.tyonakaisan.example.command.CommandFactory;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Set;

@DefaultQualifier(NonNull.class)
@Singleton
public final class Example extends JavaPlugin {

    private final Injector injector;

    @Inject
    public Example(
            final Injector bootstrapInjector
    ) {
        this.injector = bootstrapInjector.createChildInjector(new ExampleModule(this));

        ExampleProvider.register(this);
    }

    @Override
    public void onEnable() {

        final Set<Listener> listeners = this.injector.getInstance(Key.get(new TypeLiteral<>() {}));
        listeners.forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        this.injector.getInstance(CommandFactory.class).registerViaEnable(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    Injector injector() {
        return this.injector;
    }
}
