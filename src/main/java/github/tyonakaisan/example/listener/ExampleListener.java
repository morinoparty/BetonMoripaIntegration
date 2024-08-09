package github.tyonakaisan.example.listener;

import com.google.inject.Inject;
import github.tyonakaisan.example.Example;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class ExampleListener implements Listener {
    private final Example example;

    @Inject
    public ExampleListener (
            final Example example
    ) {
        this.example = example;
    }

    @EventHandler
    public void onEvent(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            var arrow = (AbstractArrow) event.getProjectile();
            var damage = arrow.getDamage();
            var force = event.getForce();
            var knock= arrow.getKnockbackStrength();

            player.sendMessage(MiniMessage.miniMessage().deserialize("""
                            <aqua>=====================</aqua>
                            damage: <damage>
                            force: <force>""",
                    Formatter.number("damage", damage),
                    Formatter.number("force", force),
                    Formatter.number("knock", knock)
            ));
        }
    }
}
