package me.whitehatd.Dodgeball.listener;

import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.Game;
import me.whitehatd.Dodgeball.game.GameState;
import me.whitehatd.Dodgeball.utils.FixedLocations;
import me.whitehatd.Dodgeball.utils.listener.ListenerBase;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class PlayerJoinLeaveListener extends ListenerBase {

    public PlayerJoinLeaveListener(Core core) {
        super(core);
    }

    @EventHandler
    public void onLeaveInGame(PlayerQuitEvent e){
        Game game = core.getGameManager().getGame(e.getPlayer());
        if(game == null)
            return;

        if(game.getCreator().getUniqueId().equals(e.getPlayer().getUniqueId())){
            if(game.getState() == GameState.WAITING) {
                game.destroy();
                return;
            }
        }

        //if last player leaves then destroy the game
        if(game.getAllPlayers().keySet().size() == 1){
            game.destroy();
            return;
        }


        if(game.getBlueTeam().containsKey(e.getPlayer().getUniqueId())) {

            game.getBlueTeam().remove(e.getPlayer().getUniqueId());

            game.getAllPlayers().keySet()
                    .forEach(all -> core.getChatUtil().message(
                            Bukkit.getPlayer(all),
                            "&3" + e.getPlayer().getName() + " &eleft!"));

            core.getMiscUtil().checkLost(game, true);

        }
        else{
            game.getRedTeam().remove(e.getPlayer().getUniqueId());

            game.getAllPlayers().keySet()
                    .forEach(all -> core.getChatUtil().message(
                            Bukkit.getPlayer(all),
                            "&c" + e.getPlayer().getName() + " &eleft!"));

            core.getMiscUtil().checkLost(game, false);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        FixedLocations.SPAWN.teleport(e.getPlayer());
    }

}
