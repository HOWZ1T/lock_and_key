package tasks;

import ca.howz1t.lock_and_key.Main;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import utils.Utils;

import java.io.IOException;

public class SaveLocksTask implements Runnable {
    private final Main plugin;

    public SaveLocksTask(JavaPlugin plugin)
    {
        this.plugin = (Main) plugin;
    }

    @Override
    public void run()
    {
        try
        {
            Utils.println("Running locks autosave...");
            plugin.getLockManager().saveLocks(plugin.getLocksPath());
            Utils.println("Locks autosave done!");
        }
        catch(IOException e)
        {
            Utils.error("Locks autosave FAILED!");
        }
    }
}
