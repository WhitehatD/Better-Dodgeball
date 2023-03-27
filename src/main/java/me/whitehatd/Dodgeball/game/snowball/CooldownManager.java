package me.whitehatd.Dodgeball.game.snowball;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class CooldownManager extends HashMap<Player, Long> {

    public void set(Player player, int ms){
        put(player, System.currentTimeMillis() + ms);
    }

    public boolean has(Player player){
        if(!containsKey(player))
            return false;

        if(System.currentTimeMillis() >= get(player))
            return false;

        return true;
    }
}
