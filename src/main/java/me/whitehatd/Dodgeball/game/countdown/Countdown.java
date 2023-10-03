package me.whitehatd.Dodgeball.game.countdown;

import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Countdown {

    private int secondsLeft;
    private final Core core;
    private final Game game;

    public Countdown(Game game, int seconds, Core core){
        this.core = core;
        this.game = game;
        this.secondsLeft = seconds;

    }

    public abstract void execute();

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (secondsLeft == -1) {
                    cancel();
                    return;
                }

                if(game.isDestroyed()){
                    cancel();
                    return;
                }
                execute();

                secondsLeft--;
            }
        }.runTaskTimer(core, 2L, 20L);
    }



    public int getSecondsLeft(){
        return secondsLeft;
    }

    public ChatColor getColor(){
        switch (secondsLeft){
            case 3 -> {
                return ChatColor.YELLOW;
            }
            case 2 -> {
                return ChatColor.RED;
            }
            case 1 -> {
                return ChatColor.DARK_RED;
            }
            case 0 -> {
                return ChatColor.DARK_GREEN;
            }
            default -> {
                return ChatColor.GREEN;
            }
        }
    }

    public Sound getSound(){
        switch (secondsLeft) {
            case 3, 2, 1 -> {
                return Sound.BLOCK_NOTE_BLOCK_BASS;
            }
            case 0 -> {
                return Sound.BLOCK_NOTE_BLOCK_BELL;
            }
            default -> {
                return null;
            }
        }
    }


    public Core getCore() {
        return core;
    }

    public Game getGame() {
        return game;
    }

}
