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
import java.util.Optional;
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
    public void generateWorld(String worldName){

        SlimePropertyMap propertyMap = new SlimePropertyMap();
        propertyMap.setValue(SlimeProperties.DIFFICULTY, "normal");
        propertyMap.setValue(SlimeProperties.ALLOW_ANIMALS, false);
        propertyMap.setValue(SlimeProperties.ENVIRONMENT, "normal");

        this.slimePlugin.asyncCreateEmptyWorld(this.slimeLoader, worldName, true, propertyMap)
                .exceptionally(th -> {
                    th.printStackTrace();

                    return java.util.Optional.empty();
                }).thenAccept(world -> {
                    world.ifPresent(slimeWorld -> this.taskQueue.add(() -> this.slimePlugin.generateWorld(slimeWorld)));
                });
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
