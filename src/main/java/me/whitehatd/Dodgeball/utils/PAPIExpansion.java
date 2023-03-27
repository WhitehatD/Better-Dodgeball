package me.whitehatd.Dodgeball.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.Game;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIExpansion extends PlaceholderExpansion {

    private final Core core;

    public PAPIExpansion(Core core){
        this.core = core;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "dodgeball";
    }

    @Override
    public @NotNull String getAuthor() {
        return "whitehatd";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.equalsIgnoreCase("alive_players_red")){
            if(!player.isOnline()) return "";
            Game game = core.getGameManager().getGame((Player) player);
            if(game == null)
                return "Not in a game";

            int redLeft = game.getRedTeam().keySet().stream().filter(target -> game.getRedTeam().get(target)).toList().size();
            return redLeft + "";
        }
        if(params.equalsIgnoreCase("alive_players_blue")){
            if(!player.isOnline()) return "";
            Game game = core.getGameManager().getGame((Player) player);
            if(game == null)
                return "Not in a game";

            int blueLeft = game.getRedTeam().keySet().stream().filter(target -> game.getBlueTeam().get(target)).toList().size();
            return blueLeft + "";
        }
        if(params.equalsIgnoreCase("alive_players_your_team")){
            if(!player.isOnline()) return "";
            Game game = core.getGameManager().getGame((Player) player);
            if(game == null)
                return "Not in a game";

            if(game.getRedTeam().containsKey((Player) player)){
                return game.getRedTeam().keySet().stream().filter(target -> game.getRedTeam().get(target)).toList().size() + "";
            } else return game.getRedTeam().keySet().stream().filter(target -> game.getBlueTeam().get(target)).toList().size() + "";
        }
        if(params.equalsIgnoreCase("alive_players_enemy_team")){
            if(!player.isOnline()) return "";
            Game game = core.getGameManager().getGame((Player) player);
            if(game == null)
                return "Not in a game";

            if(!game.getRedTeam().containsKey((Player) player)){
                return game.getRedTeam().keySet().stream().filter(target -> game.getRedTeam().get(target)).toList().size() + "";
            } else return game.getRedTeam().keySet().stream().filter(target -> game.getBlueTeam().get(target)).toList().size() + "";
        }

        return null;
    }
}
