package github.tyonakaisan.extrabeton.integration.quickshop;

import com.ghostchu.quickshop.api.event.economy.ShopPurchaseEvent;
import com.ghostchu.quickshop.api.shop.ShopType;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class QuickShopSellObjective extends Objective implements Listener {

    public QuickShopSellObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @EventHandler
    public void onSell(final ShopPurchaseEvent event) {
        event.getPurchaser()
                .getBukkitPlayer()
                .ifPresent(player -> {
                    final var profile = PlayerConverter.getID(player);
                    if (this.containsPlayer(profile) && this.checkConditions(profile) && event.getShop().getShopType().equals(ShopType.BUYING)) {
                        this.completeObjective(profile);
                    }
                });
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
