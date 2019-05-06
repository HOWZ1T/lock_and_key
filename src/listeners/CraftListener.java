package listeners;

import items.Key;
import items.ShareKey;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import utils.Utils;

public class CraftListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onCraftItem(CraftItemEvent event)
    {
        ItemStack[] items = event.getInventory().getMatrix();

        for(ItemStack item : items)
        {
            if (item.getType() == Material.TRIPWIRE_HOOK && item.containsEnchantment(Enchantment.ARROW_INFINITE))
            {
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage(Utils.Colors.RED.getRaw() + "You cannot use keys in as crafting ingredients!");
                break;
            }
        }
    }
}
