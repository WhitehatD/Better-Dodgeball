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

    public void heal(Player player){
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(pot -> player.removePotionEffect(pot.getType()));
        player.setGameMode(GameMode.SURVIVAL);
    }
}
