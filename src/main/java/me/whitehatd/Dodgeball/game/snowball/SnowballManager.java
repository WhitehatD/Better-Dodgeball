package me.whitehatd.Dodgeball.game.snowball;

import me.whitehatd.Dodgeball.game.Game;
import me.whitehatd.Dodgeball.utils.menu.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SnowballManager {

    private final Game game;
    private HashMap<Snowball, List<Location>> snowballs;

    public SnowballManager(Game game){
        this.game = game;
        this.snowballs = new HashMap<>();
    }

    public void spawnRandomly(){

        boolean redFirst = new Random().nextBoolean();

        for(int i = 1; i<= game.getSnowballsAmount(); i++){
            //half go on the red side, half on blue side
            if(i <= game.getSnowballsAmount() / 2){
                if(redFirst)
                    game.getWorld().dropItem(
                            game.getSpawnpointManager().randomLocation(game.getAreaManager().getRedRegion(), true),
                            new ItemBuilder(Material.SNOWBALL).build());
                else game.getWorld().dropItem(
                        game.getSpawnpointManager().randomLocation(game.getAreaManager().getBlueRegion(), false),
                        new ItemBuilder(Material.SNOWBALL).build());

            } else {
                if(redFirst)
                    game.getWorld().dropItem(
                            game.getSpawnpointManager().randomLocation(game.getAreaManager().getBlueRegion(), false),
                            new ItemBuilder(Material.SNOWBALL).build());
                else game.getWorld().dropItem(
                        game.getSpawnpointManager().randomLocation(game.getAreaManager().getRedRegion(), true),
                        new ItemBuilder(Material.SNOWBALL).build());
            }
        }
    }

    public HashMap<Snowball, List<Location>> getSnowballs() {
        return snowballs;
    }
}
