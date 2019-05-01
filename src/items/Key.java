package items;

import interfaces.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class Key implements Item {

    public static String key = "key";
    public static String displayName = Utils.chat(Utils.Colors.GOLD.getVal() + "Key");
    public ItemStack item;
    public ItemMeta meta;
    public ShapelessRecipe recipe;
    public NamespacedKey nskey;

    public Key(Plugin plugin) {
        this.item = new ItemStack(Material.TRIPWIRE_HOOK);
        this.meta = this.item.getItemMeta();

        int randInt = new Random().nextInt((10_001)); // gets a random int between 0 and 10_000

        ArrayList<String> lore = new ArrayList<>();
        lore.add("Used to lock/unlock interactable blocks.");
        lore.add(Utils.hideLore(Integer.toString(randInt))); // this makes each key unstackable

        this.meta.setDisplayName(Key.displayName);
        this.meta.setLore(lore);
        this.meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        this.item.setItemMeta(meta);

        this.nskey = new NamespacedKey(plugin, Key.key);
        this.recipe = new ShapelessRecipe(this.nskey, item);
        this.recipe.addIngredient(Material.TRIPWIRE_HOOK);
        this.recipe.addIngredient(Material.IRON_NUGGET);

        Bukkit.getServer().addRecipe(this.recipe);
    }

    public ItemStack getItemStack() {
        return this.item;
    }
}
