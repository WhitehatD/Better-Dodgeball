package me.whitehatd.Dodgeball.game.snowball;

import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.Game;
import me.whitehatd.Dodgeball.utils.menu.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SnowballTask extends BukkitRunnable {

    private final Game game;

    public SnowballTask(Game game){
        this.game = game;
    }

    @Override
    public void run() {
        List<Snowball> toRemove = new ArrayList<>();
        for (Snowball snowball : game.getSnowballManager().getSnowballs().keySet()) {
            List<Location> oldLocations = game.getSnowballManager().getSnowballs().get(snowball);
            List<Location> reversedLocations = new ArrayList<>(oldLocations);
            Collections.reverse(reversedLocations);

            Location currentLocation = snowball.getLocation();

            if (isSafeExtended(currentLocation)) {
                oldLocations.add(currentLocation);
                game.getSnowballManager().getSnowballs().put(snowball, oldLocations);

                continue;
            }

            Optional<Location> optionalSafeLocation =
                    reversedLocations
                            .stream()
                            .filter(this::isSafe)
                            .findFirst();

            ///there will always be a safe location
            Location safeLocation = optionalSafeLocation.get();
            //if outside game, drop
            if (game.getAreaManager().getBlueRegion().contains(safeLocation.getBlockX(), safeLocation.getBlockZ())) {
                safeLocation.setY(game.getAreaManager().getBlueRegion().getMinimumY() + 1);
            } else safeLocation.setY(game.getAreaManager().getRedRegion().getMinimumY() +1);

            Item item = game.getWorld().dropItem(safeLocation, new ItemBuilder(Material.SNOWBALL).build());

            toRemove.add(snowball);
        }

        for(Snowball snowball : toRemove){
            snowball.remove();
            game.getSnowballManager().getSnowballs().remove(snowball);
        }

    }

    private boolean isSafeExtended(Location location){
        if (game.getAreaManager().getExtendedBlueRegion().contains(
                location.getBlockX(), location.getBlockZ()) ||
                game.getAreaManager().getExtendedRedRegion().contains(
                        location.getBlockX(), location.getBlockZ()))
            return true;

        return false;
    }
    private boolean isSafe(Location location){
        if (game.getAreaManager().getBlueRegion().contains(
                location.getBlockX(), location.getBlockZ()) ||
                game.getAreaManager().getRedRegion().contains(
                        location.getBlockX(), location.getBlockZ()))
            return true;

        return false;
    }
}
