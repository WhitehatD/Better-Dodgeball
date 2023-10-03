package me.whitehatd.Dodgeball.game;

import me.whitehatd.Dodgeball.Core;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameManager {

    private List<Game> games;
    private final Core core;

    public int MAX_PLAYERS = 20;

    public GameManager(Core core){
        this.core = core;
        games = new ArrayList<>();
    }


    public void add(Game game){
        games.add(game);
    }

    public void remove(Game game){
        games.remove(game);
    }

    public List<Game> getWaitingGames(){
        return games
                .stream()
                .filter(game -> game.getState() == GameState.WAITING)
                .collect(Collectors.toList());
    }

    public Game getGame(Player player){
        return games
                .stream()
                .filter(game -> game.getAllPlayers().containsKey(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }


    public List<Game> getAllGames(){
        return games;
    }
}
