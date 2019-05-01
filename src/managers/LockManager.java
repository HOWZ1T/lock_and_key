package managers;

import objects.Lock;
import org.bukkit.World;
import utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LockManager {

    private List<Lock> normalLocks = new ArrayList<>();
    private List<Lock> netherLocks = new ArrayList<>();
    private List<Lock> endLocks = new ArrayList<>();

    public LockManager(String fp) throws IOException
    {
        loadLocks(fp);
    }

    public boolean hasLock(int x, int y, int z, World.Environment env)
    {
        switch(env)
        {
            case NORMAL:
                for(Lock lock : normalLocks)
                {
                    if(lock.getX() == x && lock.getY() == y && lock.getZ() == z)
                    {
                        return true;
                    }
                }
                break;

            case NETHER:
                for(Lock lock : netherLocks)
                {
                    if(lock.getX() == x && lock.getY() == y && lock.getZ() == z)
                    {
                        return true;
                    }
                }
                break;

            case THE_END:
                for(Lock lock : endLocks)
                {
                    if(lock.getX() == x && lock.getY() == y && lock.getZ() == z)
                    {
                        return true;
                    }
                }
                break;
        }

        return false;
    }

    public boolean hasLock(int x, int y, int z, World.Environment env, String owner)
    {
        switch(env)
        {
            case NORMAL:
                for(Lock lock : normalLocks)
                {
                    if(lock.getX() == x && lock.getY() == y && lock.getZ() == z && lock.isOwner(owner))
                    {
                        return true;
                    }
                }
                break;

            case NETHER:
                for(Lock lock : netherLocks)
                {
                    if(lock.getX() == x && lock.getY() == y && lock.getZ() == z && lock.isOwner(owner))
                    {
                        return true;
                    }
                }
                break;

            case THE_END:
                for(Lock lock : endLocks)
                {
                    if(lock.getX() == x && lock.getY() == y && lock.getZ() == z && lock.isOwner(owner))
                    {
                        return true;
                    }
                }
                break;
        }

        return false;
    }

    public Lock getLock(int x, int y, int z, World.Environment env)
    {
        switch(env)
        {
            case NORMAL:
                for(Lock lock : normalLocks)
                {
                    if(lock.getX() == x && lock.getY() == y && lock.getZ() == z)
                    {
                        return lock;
                    }
                }
                break;

            case NETHER:
                for(Lock lock : netherLocks)
                {
                    if(lock.getX() == x && lock.getY() == y && lock.getZ() == z)
                    {
                        return lock;
                    }
                }
                break;

            case THE_END:
                for(Lock lock : endLocks)
                {
                    if(lock.getX() == x && lock.getY() == y && lock.getZ() == z)
                    {
                        return lock;
                    }
                }
                break;
        }
        
        return null;
    }

    public void addLock(int x, int y, int z, World.Environment environment, String owner)
    {
        if(!hasLock(x, y ,z, environment))
        {
            switch (environment)
            {
                case NORMAL:
                    normalLocks.add(new Lock(x, y, z, environment, owner));
                    break;

                case NETHER:
                    netherLocks.add(new Lock(x, y, z, environment, owner));
                    break;

                case THE_END:
                    endLocks.add(new Lock(x, y, z, environment, owner));
                    break;
            }
        }
    }

    public void addLock(int x, int y, int z, World.Environment environment, String owner, List<String> players)
    {
        if(!hasLock(x, y ,z, environment))
        {
            switch (environment)
            {
                case NORMAL:
                    normalLocks.add(new Lock(x, y, z, environment, owner, players));
                    break;

                case NETHER:
                    netherLocks.add(new Lock(x, y, z, environment, owner, players));
                    break;

                case THE_END:
                    endLocks.add(new Lock(x, y, z, environment, owner, players));
                    break;
            }
        }
    }

    public void removeLock(int x, int y, int z, World.Environment env)
    {
        if(hasLock(x, y, z, env))
        {
            switch (env)
            {
                case NORMAL:
                    Utils.println("Removed a locked entity in " + env.name() + " at " + Integer.toString(x) + ", " +
                            Integer.toString(y) + ", " + Integer.toString(z));
                    normalLocks.remove(getLock(x, y, z, env));
                    break;

                case NETHER:
                    Utils.println("Removed a locked entity in " + env.name() + " at " + Integer.toString(x) + ", " +
                        Integer.toString(y) + ", " + Integer.toString(z));
                    netherLocks.remove(getLock(x, y, z, env));
                    break;

                case THE_END:
                    Utils.println("Removed a locked entity in " + env.name() + " at " + Integer.toString(x) + ", " +
                            Integer.toString(y) + ", " + Integer.toString(z));
                    endLocks.remove(getLock(x, y, z, env));
                    break;
            }
        }
    }

    private void loadLocks(String fp) throws IOException
    {
        Utils.println("loading locks from " + fp);

        FileReader fileReader = new FileReader(fp);
        BufferedReader reader = new BufferedReader(fileReader);

        String line = "";
        int lineIndex = 0;
        int lockCount = 0;
        while ((line = reader.readLine()) != null)
        {
            lineIndex++;
            line = line.trim();
            // if line is not a comment
            if (line.charAt(0) != '#')
            {
                String[] parts = line.split(",");
                // locks have exactly 6 parts if has sharing players or 5 parts if no other players,
                // if a line doesn't meet these requirements than report it as corrupted!
                if (parts.length != 6 && parts.length != 5)
                {
                    Utils.error("Corrupted lock entry at line: " + Integer.toString(lineIndex));
                }
                else
                {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int z = Integer.parseInt(parts[2]);

                    String env = parts[3];
                    String owner = parts[4];
                    if (parts.length == 6)
                    {
                        List<String> players = new ArrayList<>(Arrays.asList(parts[5].split(";")));
                        addLock(x, y, z, World.Environment.valueOf(env.toUpperCase()), owner, players);
                    }
                    else
                    {
                        addLock(x, y, z, World.Environment.valueOf(env.toUpperCase()), owner);
                    }

                    lockCount++;
                }
            }
        }

        Utils.println("Loaded " + Integer.toString(lockCount) + " locks.");

        reader.close();
        fileReader.close();
    }

    private String lockToString(Lock lock)
    {
        String x, y, z, owner, env, players = "";
        x = Integer.toString(lock.getX());
        y = Integer.toString(lock.getY());
        z = Integer.toString(lock.getZ());
        env = lock.getEnv().name();
        owner = lock.getOwner();

        if (lock.getPlayers().size() > 0)
        {
            for(String player : lock.getPlayers())
            {
                players += player + ";";
            }

            players = players.substring(0, players.length()-1);
        }

        return x + "," + y + "," + z + "," + env + "," + owner + "," + players + "\n";
    }

    public void saveLocks(String fp) throws IOException
    {
        Utils.println("Saving locks to " + fp);
        FileWriter fileWriter = new FileWriter(fp, false);
        int count = 0;
        for(Lock lock : normalLocks)
        {
            fileWriter.write(lockToString(lock));
            count++;
        }

        for(Lock lock : netherLocks)
        {
            fileWriter.write(lockToString(lock));
            count++;
        }

        for(Lock lock : endLocks)
        {
            fileWriter.write(lockToString(lock));
            count++;
        }

        fileWriter.close();
        Utils.println("Saved " + Integer.toString(count) + " locks.");
    }
}