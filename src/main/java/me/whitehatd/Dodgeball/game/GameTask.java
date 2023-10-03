package me.whitehatd.Dodgeball.game;

import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.countdown.GameCountdown;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class GameTask extends BukkitRunnable {

    private final Core core;

    public GameTask(Core core){
        this.core = core;
    }

    @Override
    public void run() {
        for(Game game: core.getGameManager().getAllGames()){

            if(game.isDestroyed())
                continue;

            if(game.getWorld() != null) {
                game.getWorld().setTime(0);
                game.getWorld().setClearWeatherDuration(1000);
            }

            switch (game.getState()){
                case WAITING -> {

                    if(game.getAllPlayers().size() == core.getGameManager().MAX_PLAYERS){
                        new GameCountdown(game, 5, core).start();
                        game.setState(GameState.STARTING);

                        return;
                    }

                    for(UUID uuid : game.getAllPlayers().keySet()){
                        Player player = Bukkit.getPlayer(uuid);
                        player.setFoodLevel(20);

                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacyText(
                                        core.getChatUtil().toColor(
                                                "&6Waiting for players... &e(&c" + game.getAllPlayers().size() + " &d/ &a" + core.getGameManager().MAX_PLAYERS + "&e)")));
                    }
                }
                case ONGOING -> {
                    game.getAreaManager().checkTeamsInsideAreas();
                    game.getAreaManager().checkSpectatorsInsideArea();

                    for(UUID uuid : game.getAllPlayers().keySet()) {
                        Player player = Bukkit.getPlayer(uuid);
                        player.setFoodLevel(20);

                        int blueLeft = game.getBlueTeam().keySet().stream().filter(target -> game.getBlueTeam().get(target)).toList().size();
                        int redLeft = game.getRedTeam().keySet().stream().filter(target -> game.getRedTeam().get(target)).toList().size();

                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacyText(
                                        core.getChatUtil().toColor(
                                        "&ePlayers left: &3&l" + blueLeft + " &a/ &c&l" + redLeft)));
                    }
                }
                case ENDING -> {
                    for(UUID uuid : game.getAllPlayers().keySet()) {
                        Player player = Bukkit.getPlayer(uuid);
                        player.setFoodLevel(20);

                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacyText(
                                        core.getChatUtil().toColor(
                                                "&eEnding game...")));
                    }
                }
            }
        }
    }
}
