package github.tyonakaisan.example;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import github.tyonakaisan.example.command.ExampleCommand;
import github.tyonakaisan.example.command.commands.ExampleRootCommand;
import github.tyonakaisan.example.listener.ExampleListener;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.nio.file.Path;

@SuppressWarnings("UnstableApiUsage")
@DefaultQualifier(NonNull.class)
public final class BootstrapModule extends AbstractModule {

    private final BootstrapContext context;

    BootstrapModule(
            final BootstrapContext context
    ) {
        this.context = context;
    }

    @Override
    public void configure() {
        this.bind(PluginMeta.class).toInstance(this.context.getPluginMeta());
        this.bind(ComponentLogger.class).toInstance(this.context.getLogger());
        this.bind(Path.class).toInstance(this.context.getDataDirectory());

        this.configureListener();
        this.configureCommand();
    }

    private void configureListener() {
        final Multibinder<Listener> listeners = Multibinder.newSetBinder(this.binder(), Listener.class);
        listeners.addBinding().to(ExampleListener.class).in(Scopes.SINGLETON);
    }

    private void configureCommand() {
        final Multibinder<ExampleCommand> commands = Multibinder.newSetBinder(this.binder(), ExampleCommand.class);
        commands.addBinding().to(ExampleRootCommand.class).in(Scopes.SINGLETON);
    }
}
