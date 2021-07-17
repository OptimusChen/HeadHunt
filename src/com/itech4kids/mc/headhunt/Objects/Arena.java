package com.itech4kids.mc.headhunt.Objects;

import com.itech4kids.mc.headhunt.HeadHunt;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.PluginIdentifiableCommand;
import sun.plugin2.main.server.Plugin;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.stream.DoubleStream;

public class Arena {
    private Location[] corners = new Location[2];
    private HeadHunt headhunt;
    private World world;
    private boolean isSetup;

    public Arena(HeadHunt headhunt) {
        this.headhunt = headhunt;
        world = null;
        isSetup = false;
        corners[0] = null;
        corners[1] = null;

    }

    public void setCorner(int index, Location location) throws Exception {
        corners[index - 1] = location;
        if (world == null) {
            world = location.getWorld();
        } else {
            if (world != location.getWorld()) {
                throw new Exception("The spawn locations for the arena have to be in the same world");
            }
        }

        if ( (corners[0] != null) && (corners[1] != null) ) {
            isSetup = true;
        }
    }

    public Location getCorner(int index) {
        return corners[index-1];
    }

    public boolean isReady() {
        return isSetup;
    }

    public Location getASpawnLocation() {
        Random rand = new Random();

        double x = rand.nextDouble() * (corners[1].getX() - corners[0].getX()) + corners[0].getX();
        double y = rand.nextDouble() * (corners[1].getY() - corners[0].getY()) + corners[0].getY();
        double z = rand.nextDouble() * (corners[1].getZ() - corners[0].getZ()) + corners[0].getZ();
        World w = corners[0].getWorld();
        Location location = new Location(w, x, y, z);
        return location;

    }
}
