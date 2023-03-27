package me.whitehatd.Dodgeball.game.manager;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.whitehatd.Dodgeball.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class AreaManager {

    private final Game game;
    private CuboidRegion blueRegion, redRegion;
    private CuboidRegion extendedBlueRegion, extendedRedRegion;
    private Location fallbackBlue, fallbackRed;
    private Location spectator;

    public AreaManager(Game game){
        this.game = game;

        blueRegion = new CuboidRegion(
                BlockVector3.at(
                        game.getCore().getConfig().getInt("game.blue-team.area.pos1.x"),
                        game.getCore().getConfig().getInt("game.blue-team.area.pos1.y"),
                        game.getCore().getConfig().getInt("game.blue-team.area.pos1.z")),
                BlockVector3.at(
                        game.getCore().getConfig().getInt("game.blue-team.area.pos2.x"),
                        game.getCore().getConfig().getInt("game.blue-team.area.pos2.y"),
                        game.getCore().getConfig().getInt("game.blue-team.area.pos2.z")
                ));

        extendedBlueRegion = new CuboidRegion(
                blueRegion.getMinimumPoint().add(-1, 0, -1),
                blueRegion.getMaximumPoint().add(1, 0, 1));

        fallbackBlue = new Location(
                game.getWorld(),
                game.getCore().getConfig().getInt("game.blue-team.fallback-spawn.x"),
                game.getCore().getConfig().getInt("game.blue-team.fallback-spawn.y"),
                game.getCore().getConfig().getInt("game.blue-team.fallback-spawn.z"),
                (float) game.getCore().getConfig().getDouble("game.blue-team.fallback-spawn.yaw"),
                0);

        redRegion = new CuboidRegion(
                BlockVector3.at(
                        game.getCore().getConfig().getInt("game.red-team.area.pos1.x"),
                        game.getCore().getConfig().getInt("game.red-team.area.pos1.y"),
                        game.getCore().getConfig().getInt("game.red-team.area.pos1.z")),
                BlockVector3.at(
                        game.getCore().getConfig().getInt("game.red-team.area.pos2.x"),
                        game.getCore().getConfig().getInt("game.red-team.area.pos2.y"),
                        game.getCore().getConfig().getInt("game.red-team.area.pos2.z")
                ));

        extendedRedRegion = new CuboidRegion(
                redRegion.getMinimumPoint().add(-1, 0, -1),
                redRegion.getMaximumPoint().add(1, 0, 1));

        fallbackRed = new Location(
                game.getWorld(),
                game.getCore().getConfig().getInt("game.red-team.fallback-spawn.x"),
                game.getCore().getConfig().getInt("game.red-team.fallback-spawn.y"),
                game.getCore().getConfig().getInt("game.red-team.fallback-spawn.z"),
                (float) game.getCore().getConfig().getDouble("game.red-team.fallback-spawn.yaw"),
                0);

        spectator = new Location(
                game.getWorld(),
                game.getCore().getConfig().getInt("game.spectator.spawn.x"),
                game.getCore().getConfig().getInt("game.spectator.spawn.y"),
                game.getCore().getConfig().getInt("game.spectator.spawn.z"),
                (float) game.getCore().getConfig().getDouble("game.spectator.spawn.yaw"),
                0);
    }

    public void checkTeamsInsideAreas(){
        for(Player player : game.getBlueTeam().keySet()) {
            //if is dead
            if(!game.getBlueTeam().get(player)) continue;

            Location location = player.getLocation();

            if(!blueRegion.contains(location.getBlockX(), location.getBlockZ())) {
                player.teleport(fallbackBlue);

            }
        }

        for(Player player : game.getRedTeam().keySet()) {
            //if is dead
            if(!game.getRedTeam().get(player)) continue;

            Location location = player.getLocation();

            if(!redRegion.contains(location.getBlockX(), location.getBlockZ()))
                player.teleport(fallbackRed);
        }
    }

    public void checkSpectatorsInsideArea(){
        for(Player player : game.getAllPlayers().keySet()){
            //if is alive
            if(game.getAllPlayers().get(player)) continue;

            Location location = player.getLocation();

            if(!extendedBlueRegion.contains(location.getBlockX(), location.getBlockZ()) &&
                    !extendedBlueRegion.contains(location.getBlockX(), location.getBlockZ())){
                player.teleport(spectator);
            }

        }
    }

    public CuboidRegion getBlueRegion() {
        return blueRegion;
    }

    public CuboidRegion getRedRegion() {
        return redRegion;
    }

    public CuboidRegion getExtendedBlueRegion() {
        return extendedBlueRegion;
    }

    public CuboidRegion getExtendedRedRegion() {
        return extendedRedRegion;
    }

    public Location getSpectator() {
        return spectator;
    }
}
