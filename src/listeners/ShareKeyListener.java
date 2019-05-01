package listeners;

import items.Key;
import items.ShareKey;
import managers.ItemManager;
import managers.LockManager;
import objects.Lock;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ShareKeyListener implements Listener {

    private LockManager lockManager;
    private ItemManager itemManager;
    private Material[] lockableMaterials = KeyListener.lockableMaterials;

    public ShareKeyListener(LockManager lockManager, ItemManager itemManager)
    {
        this.lockManager = lockManager;
        this.itemManager = itemManager;
    }

    private boolean isLockable(Material mat)
    {
        for(Material lockableMat : lockableMaterials)
        {
            if (mat == lockableMat) {return true;}
        }

        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractAtEntity(PlayerInteractEvent event)
    {
        Action action = event.getAction();
        Player player = event.getPlayer();

        handle_sharing(player, action, event);
    }

    private void handle_sharing(Player player, Action action, PlayerInteractEvent event)
    {
        World.Environment environment = player.getWorld().getEnvironment();

        if(action == Action.LEFT_CLICK_BLOCK)
        {
            // player must be holding a key
            ItemStack heldItem = null;

            if (event.getHand() == EquipmentSlot.HAND)
            {
                heldItem = player.getInventory().getItemInMainHand();
            }
            else if (event.getHand() == EquipmentSlot.OFF_HAND)
            {
                heldItem = player.getInventory().getItemInOffHand();
            }

            if (heldItem == null) {return;}
            ItemMeta meta = heldItem.getItemMeta();
            if(meta == null) {return;}
            String itemDisplayName = meta.getDisplayName();

            // if player is holding share key
            if (!itemDisplayName.equals(ShareKey.displayName)) {return;}

            Block block = event.getClickedBlock();
            boolean isLockable = isLockable(block.getType());

            if(!isLockable && player.isSneaking())
            {
                player.sendMessage(Utils.chatCol(Utils.Colors.RED) + block.getType().name() + " cannot be shared.");
                return;
            }

            // get id data
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            World.Environment env = block.getWorld().getEnvironment();

            Lock lock = lockManager.getLock(x, y, z, env);

            // if owner create a new sharable key
            if(lock.isOwner(player.getDisplayName()))
            {
                if(isLockable && player.isSneaking())
                {
                    ItemStack shareKey = itemManager.getItem("share_key").getItemStack();
                    shareKey.setAmount(1);

                    // adding block data to lore
                    List<String> itmLore = meta.getLore();
                    boolean isNew = true;

                    String blockSig = "[" + Integer.toString(x) + "," + Integer.toString(y) + "," + Integer.toString(z)
                            + "," + block.getType().name() + "," + env.name() + "]";
                    for(String lore : itmLore)
                    {
                        if(lore.contains(blockSig))
                        {
                            isNew = false;
                        }
                    }

                    if (isNew)
                    {
                        itmLore.add(blockSig);
                    }

                    meta.setLore(itmLore);
                    heldItem.setItemMeta(meta);

                    player.sendMessage(Utils.chatCol(Utils.Colors.GREEN) + "Added " + block.getType().name() + " to share key!");
                }
            }
        }
        else if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) // adding player to lock
        {
            // player must be holding a key
            ItemStack heldItem = null;

            if (event.getHand() == EquipmentSlot.HAND)
            {
                heldItem = player.getInventory().getItemInMainHand();
            }
            else if (event.getHand() == EquipmentSlot.OFF_HAND)
            {
                heldItem = player.getInventory().getItemInOffHand();
            }

            if (heldItem == null) {return;}
            ItemMeta meta = heldItem.getItemMeta();
            if(meta == null) {return;}
            String itemDisplayName = meta.getDisplayName();

            // if player is holding share key
            if (!itemDisplayName.equals(ShareKey.displayName)) {return;}

            // [1,1,1,blocktypename,envname]
            List<String> itmLore = meta.getLore();
            List<Lock> locks = new ArrayList<>();
            for(String lore : itmLore)
            {
                lore = lore.trim();
                String sig = "";
                // if a block signature exists
                if(Pattern.matches("\\[((-?)\\d+,)+.+\\]", lore))
                {
                    // extracting the signature
                    String[] parts = lore.split("\\]")[0].split("\\[");
                    sig = parts[parts.length-1];

                    // parse signature
                    // x,y,z,blockname,envname
                    String[] sigParts = sig.split(",");
                    if (sigParts.length == 5)
                    {
                        int x = Integer.parseInt(sigParts[0]);
                        int y = Integer.parseInt(sigParts[1]);
                        int z = Integer.parseInt(sigParts[2]);
                        World.Environment env = World.Environment.valueOf(sigParts[4]);
                        World world = null;
                        for(World wrld : player.getServer().getWorlds())
                        {
                            if (wrld.getEnvironment() == env)
                            {
                                world = wrld;
                                break;
                            }
                        }

                        Block block = world.getBlockAt(x, y, z);
                        if (isLockable(block.getType()))
                        {
                            Lock lock = lockManager.getLock(x, y, z, env);
                            if (lock != null)
                            {
                                locks.add(lock);
                            }
                            else
                            {
                                player.sendMessage(Utils.chatCol(Utils.Colors.RED) + sigParts[3] + " at " + sigParts[0] +
                                        ", " + sigParts[1] + ", " + sigParts[2] + " in " + env.name() + " no longer exists!");
                            }
                        }
                    }
                }
            }

            for(Lock lock : locks)
            {
                if(!lock.hasPlayer(player.getDisplayName()) && !lock.isOwner(player.getDisplayName()))
                {
                    lock.addPlayer(player.getDisplayName());
                }
                player.sendMessage(Utils.chatCol(Utils.Colors.GREEN) + "Added you to a locked entity at: "
                        + Integer.toString(lock.getX()) + ", " + Integer.toString(lock.getY()) + ", "
                        + Integer.toString(lock.getZ()) + " in Dimension: " + lock.getEnv().name() + " Owned by: " +
                        lock.getOwner());
            }
        }
    }
}
