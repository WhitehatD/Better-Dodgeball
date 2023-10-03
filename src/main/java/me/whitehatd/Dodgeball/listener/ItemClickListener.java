package me.whitehatd.Dodgeball.listener;

import de.tr7zw.nbtapi.NBTItem;
import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.Game;
import me.whitehatd.Dodgeball.game.GameState;
import me.whitehatd.Dodgeball.game.countdown.GameCountdown;
import me.whitehatd.Dodgeball.utils.listener.ListenerBase;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemClickListener extends ListenerBase {


    public ItemClickListener(Core core) {
        super(core);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Game game = core.getGameManager().getGame(player);
        if(game == null) return;

        if(e.getAction() != Action.LEFT_CLICK_BLOCK &&
                e.getAction() != Action.LEFT_CLICK_AIR &&
                e.getAction() != Action.RIGHT_CLICK_AIR &&
                e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(player.getInventory().getItemInMainHand().getType() == Material.AIR)
            return;

        NBTItem nbtItem = new NBTItem(player.getInventory().getItemInMainHand());
        if(!nbtItem.hasKey("usage"))
            return;

        e.setCancelled(true);



        if(game.getCreator().getUniqueId().equals(player.getUniqueId())){
            if(nbtItem.getString("usage").equals("destroy")) {
                game.destroy();
                return;
            }

            if(nbtItem.getString("usage").equals("start")){
                if(game.getBlueTeam().size() != game.getRedTeam().size()){
                    core.getChatUtil().message(player, "&eYou cannot start the game. There is an odd amount of players.");
                    return;
                }

                new GameCountdown(game, 5, core).start();
                game.setState(GameState.STARTING);
            }
        } else {
            if(nbtItem.getString("usage").equals("leave")){
                game.removePlayerFromTeam(player, game.getRedTeam().containsKey(player.getUniqueId()));
            }
        }
    }
}
