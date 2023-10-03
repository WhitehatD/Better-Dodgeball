package me.whitehatd.Dodgeball.utils;

import me.whitehatd.Dodgeball.game.Game;
import me.whitehatd.Dodgeball.game.GameState;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class MiscUtil {

    public ItemStack getSkull(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) item.getItemMeta();
        sm.setOwningPlayer(player);
        item.setItemMeta(sm);

        return item;
    }

    public void heal(Player player){
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(pot -> player.removePotionEffect(pot.getType()));
        player.setGameMode(GameMode.SURVIVAL);
    }

    public void checkLost(Game game, boolean checkBlue){  //if it's not blue then it's red
        HashMap<UUID, Boolean> team, oppositeTeam;
        if(checkBlue) {
            team = game.getBlueTeam();
            oppositeTeam = game.getRedTeam();
        }
        else{
            team = game.getRedTeam();
            oppositeTeam = game.getBlueTeam();
        }

        int playersLeft = team.keySet().stream().filter(team::get).toList().size();
        if(playersLeft == 0) {
            game.setState(GameState.ENDING);

            game.getAllPlayers().keySet()
                    .stream()
                    .map(Bukkit::getPlayer)
                    .forEach(all -> {
                        all.sendTitle(checkBlue ?
                                game.getCore().getChatUtil().toColor("&cRed team won!") :
                                game.getCore().getChatUtil().toColor("&bBlue team won!"), "");
                        all.playSound(all.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.15f);
                    });
            final int[] fireworkCount = {0};

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(fireworkCount[0] == 10)
                        cancel();

                    Location location = game.getSpawnpointManager().randomLocation(
                            checkBlue ?
                            game.getAreaManager().getRedRegion() :
                            game.getAreaManager().getBlueRegion(),
                            checkBlue);

                    Firework firework = game.getWorld().spawn(location, Firework.class);

                    FireworkMeta fireworkMeta = firework.getFireworkMeta();
                    FireworkEffect.Builder builder = FireworkEffect.builder();
                    builder.withTrail().withFlicker().withColor(checkBlue ? Color.RED : Color.BLUE)
                            .with(FireworkEffect.Type.BALL_LARGE);
                    fireworkMeta.addEffect(builder.build());
                    fireworkMeta.setPower(1);

                    firework.setFireworkMeta(fireworkMeta);

                    fireworkCount[0]++;
                }
            }.runTaskTimer(game.getCore(), 1L, 5L);

            Bukkit.getScheduler().runTaskLater(game.getCore(), () -> {
                Set<UUID> winners = oppositeTeam.keySet();
                game.destroy();

                winners
                        .stream()
                        .map(Bukkit::getPlayer)
                        .forEach(plr -> {
                    for(String command: game.getCore().getConfig().getStringList("game.victory-commands"))
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", plr.getName()));
                });

            }, 20 * 6L);
        }
    }
}
