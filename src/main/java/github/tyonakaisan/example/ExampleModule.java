package github.tyonakaisan.example;

import com.google.inject.AbstractModule;
import org.bukkit.Server;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class ExampleModule extends AbstractModule {

    private final Example example;

    ExampleModule(
            final Example example
    ) {
        this.example = example;
    }

    @Override
    public void configure() {
        this.bind(Server.class).toInstance(this.example.getServer());
    }
}
