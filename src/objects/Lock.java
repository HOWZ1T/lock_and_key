package objects;

import org.bukkit.World;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Lock {
    private int x, y, z;
    private World.Environment env;
    private String owner;
    private List<String> players; // list of players able to access the block

    public Lock(int x, int y, int z, World.Environment env)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.env = env;
        this.players = new ArrayList<String>();
    }

    public Lock(int x, int y, int z, World.Environment env, String owner)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.env = env;
        this.owner = owner;
        this.players = new ArrayList<String>();
    }

    public Lock(int x, int y, int z, World.Environment env, String owner, List<String> players)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.env = env;
        this.owner = owner;
        this.players = players;
    }

    public int getX() {return this.x;}
    public int getY() {return this.y;}
    public int getZ() {return this.z;}

    public World.Environment getEnv() { return env; }

    public String getOwner() {return this.owner;}
    public List<String> getPlayers() {return this.players;}

    public boolean hasPlayer(String name)
    {
        return players.contains(name);
    }

    public boolean isOwner(String name)
    {
        return this.owner.equals(name);
    }

    public void setOwner(String name)
    {
        this.owner = name;
    }

    public void addPlayer(String name)
    {
        if (hasPlayer(name))
        {
            Utils.println("[Lock] [addPlayer] Player (" + name + ") already assigned to Lock!");
            return;
        }

        players.add(name);
    }

    public void removePlayer(String name)
    {
        if (hasPlayer(name))
        {
            players.remove(name);
            return;
        }

        Utils.println("[Lock] [removePlayer] Player (" + name + ") is not assigned to Lock!");
    }
}
