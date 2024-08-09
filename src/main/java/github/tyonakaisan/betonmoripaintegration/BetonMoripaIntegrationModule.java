package github.tyonakaisan.betonmoripaintegration;

import com.google.inject.AbstractModule;
import org.bukkit.Server;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class BetonMoripaIntegrationModule extends AbstractModule {

    private final BetonMoripaIntegration betonmoripaintegration;

    BetonMoripaIntegrationModule(
            final BetonMoripaIntegration betonmoripaintegration
    ) {
        this.betonmoripaintegration = betonmoripaintegration;
    }

    @Override
    public void configure() {
        this.bind(Server.class).toInstance(this.betonmoripaintegration.getServer());
    }
}
