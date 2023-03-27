package me.whitehatd.Dodgeball.game.manager;

import com.sk89q.worldedit.regions.CuboidRegion;
import me.whitehatd.Dodgeball.game.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class SpawnpointManager {

    private final Game game;
    private HashMap<Player, Location> blueSpawns, redSpawns;
    private Location lobbyLocation;

    public SpawnpointManager(Game game){
        this.game = game;

        this.blueSpawns = new HashMap<>();
        this.redSpawns = new HashMap<>();

        this.lobbyLocation = new Location(
                game.getWorld(),
                game.getCore().getConfig().getInt("game.lobby.x"),
                game.getCore().getConfig().getInt("game.lobby.y"),
                game.getCore().getConfig().getInt("game.lobby.z"));
    }

    public void generateBlueSpawn(Player player){
        blueSpawns.put(player, randomLocation(game.getAreaManager().getBlueRegion(), false));
    }

    public void deleteBlueSpawn(Player player){
        blueSpawns.remove(player);
    }

    public void generateRedSpawn(Player player){
        redSpawns.put(player, randomLocation(game.getAreaManager().getRedRegion(), true));
    }

    public void deleteRedSpawn(Player player){
        redSpawns.remove(player);
    }

    public void teleportAll(){
        for(Player red : redSpawns.keySet()){
            Location spawn = redSpawns.get(red);
            red.teleport(spawn);
        }

        for(Player blue : blueSpawns.keySet()){
            Location spawn = blueSpawns.get(blue);
            blue.teleport(spawn);
        }
    }

    //sorry for using booleans for a team, next time I'll use an enum
    public Location randomLocation(CuboidRegion region, boolean red){
        return new Location(
                game.getWorld(),
                randomDouble(region.getMinimumX(), region.getMaximumX()),
                region.getMinimumY(),
                randomDouble(region.getMinimumZ(), region.getMaximumZ()),
                red ? (float) game.getCore().getConfig().getDouble("game.red-team.facing")
                        : (float) game.getCore().getConfig().getDouble("game.blue-team.facing"),
                0);
    }

    private double randomDouble(double start, double end){
        return start + ThreadLocalRandom.current().nextDouble(Math.abs(end - start + 1));
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }
}
