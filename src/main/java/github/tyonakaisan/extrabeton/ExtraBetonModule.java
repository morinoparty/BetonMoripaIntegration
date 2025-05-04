package github.tyonakaisan.extrabeton;

import com.google.inject.AbstractModule;
import org.bukkit.Server;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ExtraBetonModule extends AbstractModule {

    private final ExtraBeton extraBeton;

    ExtraBetonModule(
            final ExtraBeton extraBeton
    ) {
        this.extraBeton = extraBeton;
    }

    @Override
    public void configure() {
        this.bind(Server.class).toInstance(this.extraBeton.getServer());
    }
}
