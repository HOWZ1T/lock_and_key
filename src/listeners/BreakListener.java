package listeners;

import managers.LockManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

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

    private void removeLock(Block block)
    {
        if (isLockable(block))
        {
            manager.removeLock(block.getX(), block.getY(), block.getZ(), block.getWorld().getEnvironment());
        }
    }
}
