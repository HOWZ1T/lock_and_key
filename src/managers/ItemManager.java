package managers;

import interfaces.Item;
import items.Key;
import items.ShareKey;
import org.bukkit.plugin.Plugin;
import utils.Utils;

import java.util.Dictionary;
import java.util.Hashtable;

public class ItemManager {

    private Dictionary<String, Item> items = new Hashtable<>();
    private Plugin plugin;

    public ItemManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void loadItems() {
        this.addItem(Key.key, new Key(this.plugin));
        this.addItem(ShareKey.key, new ShareKey(this.plugin));
    }

    public void addItem(String key, Item item) {
        this.items.put(key, item);
        Utils.println("[item] added " + key);
    }

    public Item getItem(String key) {
        return this.items.get(key);
    }
}
