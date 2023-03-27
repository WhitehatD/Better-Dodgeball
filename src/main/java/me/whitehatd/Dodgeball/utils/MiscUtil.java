package me.whitehatd.Dodgeball.utils;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class MiscUtil {

    public ItemStack getSkull(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) item.getItemMeta();
        sm.setOwningPlayer(player);
        item.setItemMeta(sm);

        return item;
    }

    public boolean locationAreEqual(Location loc1, Location loc2) {

        if (loc1.getBlockX() != loc2.getBlockX()) return false;
        if (loc1.getBlockY() != loc2.getBlockY()) return false;
        if (loc1.getBlockZ() != loc2.getBlockZ()) return false;

        return true;

    }

    public void heal(Player player){
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(pot -> player.removePotionEffect(pot.getType()));
        player.setGameMode(GameMode.SURVIVAL);
    }
}
