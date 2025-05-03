package github.tyonakaisan.extrabeton;

import com.google.inject.AbstractModule;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.nio.file.Path;

@SuppressWarnings("UnstableApiUsage")
@DefaultQualifier(NonNull.class)
public final class ExtraBetonBootstrapModule extends AbstractModule {

    private final BootstrapContext context;

    ExtraBetonBootstrapModule(
            final BootstrapContext context
    ) {
        this.context = context;
    }

    @Override
    public void configure() {
        this.bind(PluginMeta.class).toInstance(this.context.getPluginMeta());
        this.bind(ComponentLogger.class).toInstance(this.context.getLogger());
        this.bind(Path.class).toInstance(this.context.getDataDirectory());
    }
}
