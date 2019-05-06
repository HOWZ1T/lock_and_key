package listeners;

import managers.LockManager;
import objects.Lock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.material.Door;
import utils.Utils;

public class BreakListener implements Listener {

    private static Material[] lockableMaterials = KeyListener.lockableMaterials;

    private LockManager manager;

    public BreakListener(LockManager manager)
    {
        this.manager = manager;
    }

    private boolean isLockable(Block block)
    {
        Material mat = block.getType();
        for(Material lockable : lockableMaterials)
        {
            if (mat == lockable)
            {
                return true;
            }
        }

        return false;
    }

    // block breaks
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event)
    {
        removeLock(event.getBlock());
    }

    // block explodes
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockExplodeEvent(BlockBreakEvent event)
    {
        removeLock(event.getBlock());
    }

    // block fades / melts
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFadeEvent(BlockBreakEvent event)
    {
        removeLock(event.getBlock());
    }

    // block burns
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBurnEvent(BlockBreakEvent event)
    {
        removeLock(event.getBlock());
    }

    // block is damaged
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamageEvent(BlockDamageEvent event) // handles the breaking permissions of lockable blocks
    {
        Block block = event.getBlock();
        if (isLockable(block))
        {
            Lock lock = manager.getLock(block.getX(), block.getY(), block.getZ(), block.getWorld().getEnvironment());

            if (lock != null)
            {
                Player player= event.getPlayer();
                if (!(lock.hasPlayer(player.getDisplayName()) || lock.isOwner(player.getDisplayName())))
                {
                    event.setCancelled(true);
                    player.sendMessage(Utils.chatCol(Utils.Colors.RED) + "You do not have permission to break this " +
                            block.getType().name());
                }
            }
        }
    }

    private void removeLock(Block block)
    {
        if (isLockable(block))
        {
            manager.removeLock(block.getX(), block.getY(), block.getZ(), block.getWorld().getEnvironment());
        }
    }
}
