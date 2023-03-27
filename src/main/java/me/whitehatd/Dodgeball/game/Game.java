package me.whitehatd.Dodgeball.game;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import de.tr7zw.nbtapi.NBTItem;
import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.manager.AreaManager;
import me.whitehatd.Dodgeball.game.snowball.CooldownManager;
import me.whitehatd.Dodgeball.game.snowball.SnowballManager;
import me.whitehatd.Dodgeball.game.manager.SpawnpointManager;
import me.whitehatd.Dodgeball.game.snowball.SnowballTask;
import me.whitehatd.Dodgeball.utils.FixedLocations;
import me.whitehatd.Dodgeball.utils.menu.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class Game {

    private final Core core;
    private HashMap<Player, Boolean> redTeam, blueTeam;
    private Player creator;
    private final String worldName;
    private World world;
    private GameState state;

    private SnowballTask task;

    private int snowballsAmount;

    private AreaManager areaManager;
    private SpawnpointManager spawnpointManager;
    private SnowballManager snowballManager;
    private CooldownManager cooldownManager;

    private boolean destroyed = false, hasRegisteredManagers = false;

    public Game(Player creator, Core core) {
        this.core = core;

        this.creator = creator;

        this.state = GameState.WAITING;
        this.redTeam = new HashMap<>();
        this.blueTeam = new HashMap<>();

        this.worldName = "dodgeball_" + (core.getGameManager().getAllGames().size() + 1);

        core.getGameManager().add(this);


        core.getSlimeUtils().generateWorld(worldName);

        core.getSlimeUtils().getTaskQueue().add(() -> {
            CompletableFuture
                    .runAsync(() -> {

                        Clipboard schematic = core.getSchematicUtil().getArena();
                        BlockVector3 at = BlockVector3.at(0, 100, 0);

                        try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld(worldName)))) {

                            Operation operation = new ClipboardHolder(schematic)
                                    .createPaste(session)
                                    .to(at)
                                    .build();

                            Operations.complete(operation);
                        }

                        Clipboard lobby = core.getSchematicUtil().getLobby();
                        BlockVector3 lobbyAt = BlockVector3.at(250, 100, 0);

                        try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld(worldName)))) {

                            Operation operation = new ClipboardHolder(lobby)
                                    .createPaste(session)
                                    .to(lobbyAt)
                                    .build();

                            Operations.complete(operation);
                        }
                    })
                    .thenRun(() -> {

                        Bukkit.getScheduler().runTask(core, () -> {
                            this.world = Bukkit.getWorld(worldName);

                            this.areaManager = new AreaManager(this);
                            this.spawnpointManager = new SpawnpointManager(this);
                            this.snowballManager = new SnowballManager(this);
                            this.cooldownManager = new CooldownManager();



                            this.hasRegisteredManagers = true;

                            if(new Random().nextBoolean())
                                addPlayerToBlueTeam(creator, true);
                            else addPlayerToRedTeam(creator, true);

                        });
                    });
        });


    }

    public void addPlayerToBlueTeam(Player player, boolean creator){
        blueTeam.put(player, true);
        spawnpointManager.generateBlueSpawn(player);
        player.teleport(spawnpointManager.getLobbyLocation());
        core.getMiscUtil().heal(player);

        if(!creator) {
            ItemStack leaveItem = new ItemBuilder(Material.RED_WOOL).setName("&cLeave the game").build();
            NBTItem nbtItem = new NBTItem(leaveItem);
            nbtItem.setString("usage", "leave");
            leaveItem = nbtItem.getItem();

            player.getInventory().setItem(8, leaveItem);
        } else {
            ItemStack destroyItem = new ItemBuilder(Material.BARRIER).setName("&cDestroy your game").build();
            NBTItem nbtItem = new NBTItem(destroyItem);
            nbtItem.setString("usage", "destroy");
            destroyItem = nbtItem.getItem();

            ItemStack forceStartItem = new ItemBuilder(Material.GREEN_WOOL).setName("&aForce start the game").build();
            NBTItem forceNBT = new NBTItem(forceStartItem);
            forceNBT.setString("usage", "start");
            forceStartItem = forceNBT.getItem();

            player.getInventory().setItem(0, forceStartItem);
            player.getInventory().setItem(8, destroyItem);
        }
    }

    public void removePlayerFromBlueTeam(Player player){
        blueTeam.remove(player);
        spawnpointManager.deleteBlueSpawn(player);
        FixedLocations.SPAWN.teleport(player);
        core.getMiscUtil().heal(player);
    }

    public void addPlayerToRedTeam(Player player, boolean creator){
        redTeam.put(player, true);
        spawnpointManager.generateRedSpawn(player);
        player.teleport(spawnpointManager.getLobbyLocation());
        core.getMiscUtil().heal(player);

        if(!creator) {
            ItemStack leaveItem = new ItemBuilder(Material.RED_WOOL).setName("&cLeave the game").build();
            NBTItem nbtItem = new NBTItem(leaveItem);
            nbtItem.setString("usage", "leave");
            leaveItem = nbtItem.getItem();

            player.getInventory().setItem(8, leaveItem);
        } else {
            ItemStack destroyItem = new ItemBuilder(Material.BARRIER).setName("&cDestroy your game").build();
            NBTItem nbtItem = new NBTItem(destroyItem);
            nbtItem.setString("usage", "destroy");
            destroyItem = nbtItem.getItem();

            ItemStack forceStartItem = new ItemBuilder(Material.GREEN_WOOL).setName("&aForce start the game").build();
            NBTItem forceNBT = new NBTItem(forceStartItem);
            forceNBT.setString("usage", "start");
            forceStartItem = forceNBT.getItem();

            player.getInventory().setItem(0, forceStartItem);
            player.getInventory().setItem(8, destroyItem);
        }
    }

    public void removePlayerFromRedTeam(Player player){
        redTeam.remove(player);
        spawnpointManager.deleteRedSpawn(player);
        FixedLocations.SPAWN.teleport(player);
        core.getMiscUtil().heal(player);
    }

    public void destroy(){
        for(Player player : getAllPlayers().keySet()){
            FixedLocations.SPAWN.teleport(player);
            core.getMiscUtil().heal(player);

            Bukkit.getOnlinePlayers()
                    .forEach(online -> online.showPlayer(core, player));

            if(!player.getUniqueId().equals(creator.getUniqueId()))
                core.getChatUtil().message(player, "&cYou were removed from the game");
            else core.getChatUtil().message(player, "&cThe game has been destroyed.");
        }

        if(task != null)
            task.cancel();
        destroyed = true;

        core.getGameManager().remove(this);
        core.getSlimeUtils().unloadWorld(worldName);
    }


    public GameState getState() {
        return state;
    }

    public SnowballTask startSnowballTask() {
        task = new SnowballTask(this);
        task.runTaskTimer(core, 3L, 1L);

        return task;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public HashMap<Player, Boolean> getBlueTeam() {
        return blueTeam;
    }

    public HashMap<Player, Boolean> getRedTeam() {
        return redTeam;
    }

    public HashMap<Player, Boolean> getAllPlayers(){
        HashMap<Player, Boolean> copy = new HashMap<>();
        copy.putAll(blueTeam);
        copy.putAll(redTeam);

        return copy;
    }

    public World getWorld() {
        return world;
    }

    public SpawnpointManager getSpawnpointManager() {
        return spawnpointManager;
    }

    public AreaManager getAreaManager() {
        return areaManager;
    }

    public Player getCreator() {
        return creator;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public Core getCore() {
        return core;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public SnowballManager getSnowballManager() {
        return snowballManager;
    }

    public void setSnowballsAmount(int snowballsAmount) {
        this.snowballsAmount = snowballsAmount;
    }

    public int getSnowballsAmount() {
        return snowballsAmount;
    }
}
