package interfaces;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public interface Item {
    public ItemStack item = null;
    public ItemMeta meta = null;
    public ShapedRecipe shapedRecipe = null;
    public ShapelessRecipe recipe = null;
    public NamespacedKey key = null;

    public ItemStack getItemStack();
}
