package me.whitehatd.Dodgeball.game.snowball;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import de.tr7zw.nbtapi.NBTItem;
import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.Game;
import me.whitehatd.Dodgeball.game.GameState;
import me.whitehatd.Dodgeball.utils.listener.ListenerBase;
import me.whitehatd.Dodgeball.utils.menu.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SnowballListener extends ListenerBase {


    public SnowballListener(Core core) {
        super(core);
    }

    @EventHandler
    public void cancelDespawn(ItemDespawnEvent e){
        if(!(e.getEntity() instanceof Snowball)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onShoot(PlayerInteractEvent e){
        if(e.getHand() != EquipmentSlot.HAND) return;

        if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getItem() == null) return;
        if(e.getItem().getType() != Material.SNOWBALL) return;

        Game game = core.getGameManager().getGame(e.getPlayer());
        if(game == null) return;

        if(game.getCooldownManager().has(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }
        game.getCooldownManager().set(e.getPlayer(), 300);
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent e){
        if(!(e.getEntity().getShooter() instanceof Player)) return;
        if(!(e.getEntity() instanceof Snowball)) return;

        Player player = (Player) e.getEntity().getShooter();
        Game game = core.getGameManager().getGame(player);
        if(game == null) return;

        Snowball snowball = (Snowball)e.getEntity();

        List<Location> oldLocations = new ArrayList<>();
        oldLocations.add(snowball.getLocation());
        game.getSnowballManager().getSnowballs().put(snowball.getUniqueId(), oldLocations);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e){
        if(!(e.getEntity().getShooter() instanceof Player)) return;
        if(!(e.getEntity() instanceof Snowball)) return;

        Player player = (Player) e.getEntity().getShooter();
        Game game = core.getGameManager().getGame(player);
        if(game == null) return;

        Player entity = (Player) e.getHitEntity();

        if(entity != null){
            //if same team, nothing happens
            if((game.getBlueTeam().containsKey(entity) && game.getBlueTeam().containsKey(player)) ||
                    (game.getRedTeam().containsKey(entity) && game.getRedTeam().containsKey(player))) {

                game.getSnowballManager().getSnowballs().remove(e.getEntity().getUniqueId());
                e.setCancelled(true);
                e.getEntity().remove();

                Item item = game.getWorld().dropItem(
                        e.getHitEntity().getLocation(),
                        new ItemBuilder(Material.SNOWBALL).build());

                return;
            }

            //allow flight, hide dead player from alive players, reveal dead player to other dead players, add leave item
            entity.setAllowFlight(true);
            entity.setFlying(true);

            Location entityLocation = entity.getLocation();
            Location eyeLocation = entity.getEyeLocation();

            Particle.DustOptions dust = new Particle.DustOptions(
                    Color.FUCHSIA, 3);
            game.getWorld().spawnParticle(
                    Particle.REDSTONE,
                    eyeLocation.getX(),
                    eyeLocation.getY(),
                    eyeLocation.getZ(),
                    5, 0.3, 0.3, 0.3, dust);

            entity.teleport(game.getAreaManager().getSpectator());

            entity.sendTitle(core.getChatUtil().toColor("&cYou were eliminated!"), "");
            entity.playSound(entity.getLocation(), Sound.ENTITY_BAT_HURT, 1f, 0.8f);

            game.getAllPlayers().keySet()
                    .stream()
                    .filter(alive -> game.getAllPlayers().get(alive))
                    .forEach(alive -> alive.hidePlayer(core, entity));

            game.getAllPlayers().keySet()
                    .stream()
                    .filter(dead -> !game.getAllPlayers().get(dead))
                    .forEach(dead -> dead.showPlayer(core, entity));

            game.getSnowballManager().getSnowballs().remove(e.getEntity().getUniqueId());
            e.setCancelled(true);
            e.getEntity().remove();

            Item item = game.getWorld().dropItem(
                    entityLocation,
                    new ItemBuilder(Material.SNOWBALL).build());


            ItemStack leaveItem = new ItemBuilder(Material.RED_WOOL).setName("&cLeave the game").build();
            NBTItem nbtItem = new NBTItem(leaveItem);
            nbtItem.setString("usage", "leave");
            leaveItem = nbtItem.getItem();

            entity.getInventory().clear();
            entity.getInventory().setItem(8, leaveItem);

            //no longer alive
            if(game.getBlueTeam().containsKey(entity)) {
                game.getBlueTeam().put(entity, false);

                game.getAllPlayers().keySet()
                        .forEach(all -> core.getChatUtil().message(
                                all,
                                "&3" + entity.getName() + "&e was eliminated by &c" + player.getName() + "&e!"));

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
                game.getRedTeam().put(entity, false);

                game.getAllPlayers().keySet()
                        .forEach(all -> core.getChatUtil().message(
                                all,
                                "&c" + entity.getName() + "&e was eliminated by &3" + player.getName() + "&e!"));

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



            //else entity must've hit a block
        } else {
            game.getSnowballManager().getSnowballs().remove(e.getEntity().getUniqueId());
            e.setCancelled(true);
            e.getEntity().remove();


            Item item = game.getWorld().dropItem(
                    e.getHitBlock().getLocation().clone().add(0, 1, 0),
                    new ItemBuilder(Material.SNOWBALL).build());
        }
    }
}
