package me.whitehatd.Dodgeball.listener;

import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.Game;
import me.whitehatd.Dodgeball.utils.listener.ListenerBase;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class CancelGriefListener extends ListenerBase {

    public CancelGriefListener(Core core){
        super(core);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e){
        if(e.getEntityType() != EntityType.SNOWBALL && e.getEntityType() != EntityType.DROPPED_ITEM && e.getEntityType() != EntityType.FIREWORK)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e){
        Game game = core.getGameManager().getGame(e.getPlayer());
        if(game == null) return;
        if(game.getAllPlayers().get(e.getPlayer().getUniqueId())) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void noRegenInGame(EntityRegainHealthEvent e){
        if(!(e.getEntity() instanceof Player)) return;
        if(core.getGameManager().getGame((Player) e.getEntity()) == null) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void noFallDamage(EntityDamageEvent e){
        if(e.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void noBreak(BlockBreakEvent e){
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void noBuild(BlockPlaceEvent e){
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void noGrief(PlayerInteractEvent event)
    {
        if(event.getPlayer().isOp()) return;
        if(event.getAction() == Action.PHYSICAL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void noPvP(EntityDamageByEntityEvent e){
         e.setCancelled(true);
    }

}
