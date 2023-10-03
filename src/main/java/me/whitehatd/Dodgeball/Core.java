package me.whitehatd.Dodgeball;

import com.grinderwolf.swm.api.SlimePlugin;
import me.whitehatd.Dodgeball.game.Game;
import me.whitehatd.Dodgeball.game.GameManager;
import me.whitehatd.Dodgeball.game.GameTask;
import me.whitehatd.Dodgeball.npc.NPCUtil;
import me.whitehatd.Dodgeball.utils.ChatUtil;
import me.whitehatd.Dodgeball.utils.MiscUtil;
import me.whitehatd.Dodgeball.utils.PAPIExpansion;
import me.whitehatd.Dodgeball.utils.SchematicUtil;
import me.whitehatd.Dodgeball.utils.listener.ListenerRegistration;
import me.whitehatd.Dodgeball.utils.slime.SlimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;

public class Core extends JavaPlugin {

    private ChatUtil chatUtil;

    private SlimePlugin slimePlugin;
    private SlimeUtils slimeUtils;

    private GameManager gameManager;

    private NPCUtil npcUtil;
    private MiscUtil miscUtil;
    private SchematicUtil schematicUtil;

    private static Core INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        saveDefaultConfig();

        this.chatUtil = new ChatUtil();

        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        if(slimePlugin == null){
            getChatUtil().message(Bukkit.getConsoleSender(), "&cNo SlimeWorldManager found! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        slimeUtils = new SlimeUtils(this);

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getChatUtil().message(Bukkit.getConsoleSender(), "&cNo PlaceholderAPI found! Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        new PAPIExpansion(this).register();

        this.miscUtil = new MiscUtil();
        this.npcUtil = new NPCUtil(this);
        this.schematicUtil = new SchematicUtil(this);

        new ListenerRegistration(this);

        this.gameManager = new GameManager(this);

        new GameTask(this).runTaskTimer(this, 2L, 2L);


    }

    @Override
    public void onDisable(){
        npcUtil.destroyStartNPC();

        List<Game> games = new ArrayList<>(gameManager.getAllGames());
        games.forEach(Game::destroy);
    }

    public SchematicUtil getSchematicUtil() {
        return schematicUtil;
    }

    public MiscUtil getMiscUtil() {
        return miscUtil;
    }

    public ChatUtil getChatUtil() {
        return chatUtil;
    }

    public NPCUtil getNpcUtil() {
        return npcUtil;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public SlimeUtils getSlimeUtils() {
        return slimeUtils;
    }

    public SlimePlugin getSlimePlugin() {
        return slimePlugin;
    }

    public static Core getInstance() {
        return INSTANCE;
    }
}
