package me.whitehatd.Dodgeball.game.countdown;

import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.Game;
import me.whitehatd.Dodgeball.game.GameState;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GameCountdown extends Countdown{


    public GameCountdown(Game game, int seconds, Core core){
        super(game, seconds, core);

    }

    @Override
    public void execute() {

        if(getSecondsLeft() == 0) {
            getGame().setState(GameState.ONGOING);
            getGame().getSnowballManager().spawnRandomly();
            getGame().startSnowballTask();
        }


        for (UUID uuid : getGame().getAllPlayers().keySet()) {
            Player player = Bukkit.getPlayer(uuid);

            if (getSecondsLeft() == 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(
                                getColor() + "Fight!"));
                getGame().getSpawnpointManager().teleportAll();

                player.getInventory().clear();


            } else {

                if(getSecondsLeft() == 5){
                    player.getInventory().clear();
                    getGame().setSnowballsAmount(getGame().getAllPlayers().size() / 2);
                }



                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(
                                getColor() + " The game starts in: " + getSecondsLeft() + ".."));
            }

            player.playSound(player.getLocation(), getSound(), 1f, 1f);
        }
    }


}
