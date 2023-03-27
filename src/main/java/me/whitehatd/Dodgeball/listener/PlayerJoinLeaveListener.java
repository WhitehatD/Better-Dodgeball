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


        if(game.getBlueTeam().containsKey(e.getPlayer())) {

            game.getBlueTeam().remove(e.getPlayer());

            game.getAllPlayers().keySet()
                    .forEach(all -> core.getChatUtil().message(
                            all,
                            "&3" + e.getPlayer().getName() + " &eleft!"));

            int blueLeft = game.getBlueTeam().keySet().stream().filter(target -> game.getBlueTeam().get(target)).toList().size();
            if(blueLeft == 0) {
                game.setState(GameState.ENDING);

                game.getAllPlayers().keySet()
                        .forEach(all -> {
                            all.sendTitle(core.getChatUtil().toColor("&cRed team won!"), "");
                            all.playSound(all.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.15f);
                        });
                final int[] fireworkCount = {0};

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(fireworkCount[0] == 10)
                            cancel();

                        Location location = game.getSpawnpointManager().randomLocation(game.getAreaManager().getRedRegion(), true);

                        Firework firework = game.getWorld().spawn(location, Firework.class);

                        FireworkMeta fireworkMeta = firework.getFireworkMeta();
                        FireworkEffect.Builder builder = FireworkEffect.builder();
                        builder.withTrail().withFlicker().withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE);
                        fireworkMeta.addEffect(builder.build());
                        fireworkMeta.setPower(1);

                        firework.setFireworkMeta(fireworkMeta);

                        fireworkCount[0]++;
                    }
                }.runTaskTimer(core, 1L, 5L);

                Bukkit.getScheduler().runTaskLater(game.getCore(), () -> {
                    Set<Player> winners = game.getRedTeam().keySet();
                    game.destroy();

                    winners.forEach(red -> {
                        for(String command: core.getConfig().getStringList("game.victory-commands"))
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", red.getName()));
                    });

                }, 20 * 6L);
            }

        }
        else{
            game.getRedTeam().remove(e.getPlayer());

            game.getAllPlayers().keySet()
                    .forEach(all -> core.getChatUtil().message(
                            all,
                            "&c" + e.getPlayer().getName() + " &eleft!"));

            int redLeft = game.getRedTeam().keySet().stream().filter(target -> game.getRedTeam().get(target)).toList().size();
            if(redLeft == 0) {
                game.setState(GameState.ENDING);

                game.getAllPlayers().keySet()
                        .forEach(all -> {
                            all.sendTitle(core.getChatUtil().toColor("&3Blue team won!"), "");
                            all.playSound(all.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.15f);
                        });


                final int[] fireworkCount = {0};

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(fireworkCount[0] == 10)
                            cancel();

                        Location location = game.getSpawnpointManager().randomLocation(game.getAreaManager().getBlueRegion(), false);

                        Firework firework = game.getWorld().spawn(location, Firework.class);

                        FireworkMeta fireworkMeta = firework.getFireworkMeta();
                        FireworkEffect.Builder builder = FireworkEffect.builder();
                        builder.withTrail().withFlicker().withColor(Color.BLUE).with(FireworkEffect.Type.BALL_LARGE);
                        fireworkMeta.addEffect(builder.build());
                        fireworkMeta.setPower(1);

                        firework.setFireworkMeta(fireworkMeta);

                        fireworkCount[0]++;
                    }
                }.runTaskTimer(core, 1L, 5L);

                Bukkit.getScheduler().runTaskLater(game.getCore(), () -> {
                    Set<Player> winners = game.getBlueTeam().keySet();
                    game.destroy();

                    winners.forEach(blue -> {
                        for(String command: core.getConfig().getStringList("game.victory-commands"))
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", blue.getName()));
                    });


                }, 20 * 6L);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        FixedLocations.SPAWN.teleport(e.getPlayer());
    }

}
