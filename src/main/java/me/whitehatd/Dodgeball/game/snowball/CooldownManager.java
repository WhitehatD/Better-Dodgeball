package me.whitehatd.Dodgeball.game.snowball;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager extends HashMap<UUID, Long> {

    public void set(Player player, int ms){
        put(player.getUniqueId(), System.currentTimeMillis() + ms);
    }

    public boolean has(Player player){
        if(!containsKey(player.getUniqueId()))
            return false;

        return System.currentTimeMillis() < get(player.getUniqueId());
    }
}
