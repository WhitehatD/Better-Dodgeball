package me.whitehatd.Dodgeball.utils;

import me.whitehatd.Dodgeball.Core;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum FixedLocations {

    SPAWN("locations.spawn"),
    NPC_SPAWN("start-npc.location");

    private final String worldName;
    private final double x, y, z, yaw;

    FixedLocations(String configSection){
        this.x = Core.INSTANCE.getConfig().getDouble(configSection + ".x");
        this.y = Core.INSTANCE.getConfig().getDouble(configSection + ".y");
        this.z = Core.INSTANCE.getConfig().getDouble(configSection + ".z");
        this.yaw = Core.INSTANCE.getConfig().getDouble(configSection + ".yaw");
        this.worldName = Core.INSTANCE.getConfig().getString(configSection + ".world");
    }

    public boolean teleport(Player player){
        if(Bukkit.getWorld(worldName) == null)
            return false;

        player.teleport(
                new Location(
                        Bukkit.getWorld(worldName),
                        x > 0 ? x+ 0.5 : x-0.5,
                        y,
                        z > 0 ? z + 0.5 : z - 0.5,
                        (float) yaw,
                        0));
            return true;
    }

    public Location get(){
        return new Location(
                Bukkit.getWorld(worldName),
                x > 0 ? x+ 0.5 : x-0.5,
                y,
                z > 0 ? z + 0.5 : z - 0.5,
                (float) yaw,
                0);
    }
}
