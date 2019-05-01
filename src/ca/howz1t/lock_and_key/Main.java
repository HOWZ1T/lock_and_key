package ca.howz1t.lock_and_key;

import cmds.CommandKey;
import cmds.CommandShareKey;
import listeners.BreakListener;
import listeners.KeyListener;
import listeners.ShareKeyListener;
import managers.ItemManager;
import managers.LockManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import tasks.SaveLocksTask;
import utils.Utils;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin implements Listener {
    private ItemManager itemManager;
    private LockManager lockManager = null;
    private String locksPath = "";

    @Override
    public void onEnable() {
        locksPath = getDataFolder().getAbsolutePath() + File.separator + "locks.csv";
        Utils.println("setting up lock manager...");
        try
        {
            File filepath = new File(getDataFolder().getAbsolutePath());
            boolean result = filepath.mkdirs();
            if (!result && !filepath.exists())
            {
                Utils.println("failed to make data folder!");
                Utils.error("disabling myself!");
                this.onDisable();
                return;
            }

            File file = new File(locksPath);
            if (!file.exists())
            {
                result = file.createNewFile();
                if (!result)
                {
                    Utils.println("failed to make locks file!");
                    Utils.error("disabling myself!");
                    this.onDisable();
                    return;
                }
            }
            lockManager = new LockManager(locksPath);
        }
        catch(IOException e)
        {
            Utils.error("IOException:\n" + e.getMessage());
            Utils.error("Disabling myself!");
            this.onDisable();
            return;
        }


        Utils.println("registering items...");
        itemManager = new ItemManager(this);
        itemManager.loadItems();

        Utils.println("registering commands...");
        this.getCommand("key").setExecutor(new CommandKey(itemManager));
        this.getCommand("share_key").setExecutor(new CommandShareKey(itemManager));

        Utils.println("registering listeners...");
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new KeyListener(lockManager), this);
        getServer().getPluginManager().registerEvents(new BreakListener(lockManager), this);
        getServer().getPluginManager().registerEvents(new ShareKeyListener(lockManager, itemManager), this);

        Utils.println("registering tasks...");
        BukkitScheduler scheduler = getServer().getScheduler();
        // runs autosave 100 ticks after server start and then every 6000 ticks (5 minutes) (20 ticks = 1 second)
        scheduler.scheduleSyncRepeatingTask(this, new SaveLocksTask(this), 100L, 6000L);

        Utils.println("successfully loaded.");
    }

    @Override
    public void onDisable() {
        if (lockManager != null)
        {
            try
            {
                Utils.println("saving locks to file...");
                lockManager.saveLocks(locksPath);
            }
            catch(IOException e)
            {
                Utils.error("Could not save locks!");
                Utils.error("IOException:\n" + e.getMessage());
            }
        }
    }

    public LockManager getLockManager()
    {
        return this.lockManager;
    }

    public String getLocksPath()
    {
        return this.locksPath;
    }

    // TODO SHARING EFFECT / TODO LOCK REMOVER (CROWBAR FOR OWNER) :D
}
