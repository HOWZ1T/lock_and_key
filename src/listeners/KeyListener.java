package listeners;

import items.Key;
import items.ShareKey;
import managers.LockManager;
import objects.Lock;
import org.bukkit.Location;
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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Door;
import utils.Utils;

public class KeyListener implements Listener {

    public static Material[] lockableMaterials = new Material[]{
            Material.CHEST,
            Material.FURNACE,
            Material.ANVIL,
            Material.CHIPPED_ANVIL,
            Material.DAMAGED_ANVIL,
            Material.BARREL,
            Material.BEACON,
            Material.BLAST_FURNACE,
            Material.BREWING_STAND,
            Material.CAMPFIRE,
            Material.CARTOGRAPHY_TABLE,
            Material.CAULDRON,
            Material.SMOKER,
            Material.DROPPER,
            Material.HOPPER,
            Material.DISPENSER,
            Material.GRINDSTONE,
            Material.JUKEBOX,
            Material.TRAPPED_CHEST,
            Material.ENCHANTING_TABLE,
            Material.DARK_OAK_FENCE_GATE,
            Material.ACACIA_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.OAK_FENCE_GATE,
            Material.SPRUCE_FENCE_GATE,
            Material.DARK_OAK_TRAPDOOR,
            Material.ACACIA_TRAPDOOR,
            Material.BIRCH_TRAPDOOR,
            Material.IRON_TRAPDOOR,
            Material.JUNGLE_TRAPDOOR,
            Material.OAK_TRAPDOOR,
            Material.SPRUCE_TRAPDOOR,
            Material.LECTERN,
            Material.ENDER_CHEST,
            Material.SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.BLACK_SHULKER_BOX
    };

    private LockManager lockManager;

    public KeyListener(LockManager lockManager)
    {
        this.lockManager = lockManager;
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
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        if (item != null)
        {
            Utils.println(item.getItemMeta().getDisplayName());
            Utils.println(Key.displayName);
            if (item.getItemMeta().getDisplayName().equals(Key.displayName))
            {
                event.setCancelled(true);
                player.sendMessage(Utils.chatCol(Utils.Colors.RED) + "Key cannot be placed.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractAtEntity(PlayerInteractEvent event)
    {
        Action action = event.getAction();
        Player player = event.getPlayer();

        handle_locking(player, action, event);
    }

    private void handle_locking(Player player, Action action, PlayerInteractEvent event)
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

            if (!itemDisplayName.equals(Key.displayName)) {return;}

            Block block = event.getClickedBlock();
            boolean isLockable = isLockable(block.getType());

            if(!isLockable && player.isSneaking())
            {
                player.sendMessage(Utils.chatCol(Utils.Colors.RED) + block.getType().name() + " cannot be locked.");
                return;
            }

            // get id data
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            World.Environment env = block.getWorld().getEnvironment();

            Lock lock = lockManager.getLock(x, y, z, env);
            // if player sneaks then attempt to assign player to chest
            if(player.isSneaking())
            {
                if (lock == null)
                {
                    if(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST)
                    {
                        Chest chest = (Chest) block.getState();
                        InventoryHolder ih = chest.getInventory().getHolder();
                        if(chest.getInventory().getSize() == 54) // checks if it is a double chest
                        {
                            DoubleChest dc = (DoubleChest) ih;
                            Chest left_chest = (Chest) dc.getLeftSide();
                            Chest right_chest = (Chest) dc.getRightSide();
                            lockManager.addLock(left_chest.getX(), left_chest.getY(), left_chest.getZ(),
                                    env, player.getDisplayName());
                            lockManager.addLock(right_chest.getX(), right_chest.getY(), right_chest.getZ(),
                                    env, player.getDisplayName());

                            String blockName = "DOUBLE_TRAPPED_CHEST";
                            if (block.getType() == Material.CHEST) {blockName = "DOUBLE_CHEST";}

                            player.sendMessage("Assigned the " + Utils.chatCol(Utils.Colors.GOLD) + blockName +
                                    Utils.chatCol(Utils.Colors.WHITE) + " to you");
                        }
                        else
                        {
                            lockManager.addLock(x, y, z, env, player.getDisplayName());
                            player.sendMessage("Assigned the " + Utils.chatCol(Utils.Colors.GOLD) + block.getType().name() +
                                    Utils.chatCol(Utils.Colors.WHITE) + " to you");
                        }
                    }
                    else
                    {
                        lockManager.addLock(x, y, z, env, player.getDisplayName());
                        player.sendMessage("Assigned the " + Utils.chatCol(Utils.Colors.GOLD) + block.getType().name() +
                                Utils.chatCol(Utils.Colors.WHITE) + " to you");
                    }
                    return;
                }
                else
                {
                    if(!lock.isOwner(player.getDisplayName()))
                    {
                        player.sendMessage("You do not have permission to access this " + Utils.chatCol(Utils.Colors.RED)
                                + block.getType().name() + "!");
                        event.setCancelled(true);
                    }
                }
            }

            if (lock != null)
            {
                if (lock.getOwner() != null)
                {
                    player.sendMessage("This " + Utils.chatCol(Utils.Colors.LIGHT_PURPLE) + block.getType().name() +
                            Utils.chatCol(Utils.Colors.WHITE) + " belongs to " + lock.getOwner());
                }
            }
            else
            {
                player.sendMessage("This " + Utils.chatCol(Utils.Colors.GREEN) + block.getType().name() +
                        Utils.chatCol(Utils.Colors.WHITE) + " belongs to no one.");
            }
        }
        else if (action == Action.RIGHT_CLICK_BLOCK)
        {
            Block block = event.getClickedBlock();
            boolean isLockable = isLockable(block.getType());

            if(!isLockable){return;}

            // get id data
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            World.Environment env = block.getLocation().getWorld().getEnvironment();

            Lock lock = lockManager.getLock(x, y, z, env);

            if(lock == null) {return;}
            if(!(lock.hasPlayer(player.getDisplayName()) || lock.isOwner(player.getDisplayName())))
            {
                player.sendMessage("You do not have permission to access this " + Utils.chatCol(Utils.Colors.RED)
                        + block.getType().name() + "!");
                event.setCancelled(true);
            }

            // if player is holding a key, don't let them place it
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

            if (!itemDisplayName.equals(Key.displayName) && !itemDisplayName.equals(ShareKey.displayName)) {return;}
            event.setCancelled(true);
        }
    }
}
