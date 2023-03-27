package me.whitehatd.Dodgeball.utils.slime;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.internal.com.mongodb.MongoException;
import com.grinderwolf.swm.plugin.config.ConfigManager;
import com.grinderwolf.swm.plugin.config.DatasourcesConfig;
import com.grinderwolf.swm.plugin.config.WorldData;
import com.grinderwolf.swm.plugin.config.WorldsConfig;
import me.whitehatd.Dodgeball.Core;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class SlimeUtils {

    private final Core plugin;
    private final SlimePlugin slimePlugin;
    private final BlockingQueue<Runnable> taskQueue;
    private SlimeLoader slimeLoader;

    public SlimeUtils(Core plugin) {
        this.plugin = plugin;
        this.slimePlugin = plugin.getSlimePlugin();
        this.taskQueue = new LinkedBlockingQueue<>();
        this.startTaskQueue();

        DatasourcesConfig datasourcesConfig = ConfigManager.getDatasourcesConfig();
        DatasourcesConfig.MongoDBConfig mysqlConfig = datasourcesConfig.getMongoDbConfig();
        try {
            this.slimeLoader = new FakeSlimeLoader(mysqlConfig);
        } catch (MongoException throwable) {
            throwable.printStackTrace();
        }

        this.unloadWorld("world_nether");
        this.unloadWorld("world_the_end");
    }

    public void importWorld(String worldName) {
        String path = Bukkit.getWorldContainer().getAbsolutePath();
        File worldDirectory = new File(path + File.separator + worldName);
        try {
            this.slimePlugin.importWorld(worldDirectory, worldName, this.slimeLoader);
        } catch (WorldAlreadyExistsException | InvalidWorldException | WorldLoadedException | WorldTooBigException | IOException e) {
            e.printStackTrace();
        } finally {
            WorldData worldData = ConfigManager.getWorldConfig().getWorlds().get(worldName);
            worldData.setReadOnly(true);
        }
    }

    public void loadWorld(String worldName) {
        WorldData worldData = ConfigManager.getWorldConfig().getWorlds().get(worldName);
        SlimeWorld slimeWorld = null;
        AtomicReference<SlimeWorld> slimeReference = new AtomicReference<>(null);
        try {
            slimeWorld = this.getSlimeWorld(worldName, worldData);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            slimeReference.set(slimeWorld);
            this.taskQueue.add(() -> this.slimePlugin.generateWorld(slimeReference.get()));
        }
    }

    public SlimeWorld getSlimeWorld(String worldName, WorldData worldData) throws ExecutionException, InterruptedException {
        try {
            return this.slimePlugin.loadWorld(this.slimeLoader, worldName, worldData.isReadOnly(), worldData.toPropertyMap());
        } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void cloneWorld(String sourceWorld, String worldName) {
        WorldData worldData = ConfigManager.getWorldConfig().getWorlds().get(sourceWorld);
        SlimePropertyMap slimePropertyMap = worldData.toPropertyMap();
        slimePropertyMap.setString(SlimeProperties.DIFFICULTY, "hard");
        AtomicReference<SlimeWorld> slimeReference = new AtomicReference<>();
        try {
            slimeReference.set(this.slimePlugin.loadWorld(this.slimeLoader, sourceWorld, true, slimePropertyMap).clone(worldName, this.slimeLoader));
        } catch (WorldAlreadyExistsException | IOException | UnknownWorldException | CorruptedWorldException | NewerFormatException | WorldInUseException e) {
            e.printStackTrace();
        } finally {
            this.taskQueue.add(() -> this.slimePlugin.generateWorld(slimeReference.get()));
        }
    }

    public void generateWorld(String worldName){

        SlimePropertyMap propertyMap = new SlimePropertyMap();
        propertyMap.setValue(SlimeProperties.DIFFICULTY, "normal");
        propertyMap.setValue(SlimeProperties.ALLOW_ANIMALS, false);
        propertyMap.setValue(SlimeProperties.ENVIRONMENT, "normal");


            AtomicReference<SlimeWorld> slimeWorld = new AtomicReference<>();

            try {
                slimeWorld.set(this.slimePlugin.createEmptyWorld(this.slimeLoader, worldName, true, propertyMap));
            } catch (WorldAlreadyExistsException | IOException worldAlreadyExistsException) {
                worldAlreadyExistsException.printStackTrace();
            } finally {
                this.taskQueue.add(() -> this.slimePlugin.generateWorld(slimeWorld.get()));

            }

    }

    public void deleteWorld(String worldName) {
        WorldsConfig worldsConfig = ConfigManager.getWorldConfig();
        try {
            this.slimeLoader.deleteWorld(worldName);
        } catch (UnknownWorldException | IOException e) {
            e.printStackTrace();
        } finally {
            worldsConfig.getWorlds().remove(worldName);
        }
    }

    public boolean existsWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        return world != null;
    }

    public void unloadWorld(String worldName) {
        Bukkit.unloadWorld(worldName, false);
    }

    public synchronized void startTaskQueue() {
        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            if (this.taskQueue.isEmpty()) return;
            try {
                this.taskQueue.take().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0L, 1L);
    }

    public BlockingQueue<Runnable> getTaskQueue() {
        return taskQueue;
    }
}
