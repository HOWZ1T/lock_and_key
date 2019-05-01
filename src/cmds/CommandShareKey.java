package cmds;

import managers.ItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import utils.Utils;

public class CommandShareKey implements CommandExecutor {

    private ItemManager itemManager;

    public CommandShareKey(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack key = itemManager.getItem("share_key").getItemStack();
            key.setAmount(1);
            player.getInventory().addItem(key);
            return true;
        }

        sender.sendMessage(Utils.chat(Utils.Colors.RED.getVal() + "Only players can use this command!"));
        return false;
    }
}
