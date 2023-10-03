package me.whitehatd.Dodgeball.utils.listener;

import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.game.snowball.SnowballListener;
import me.whitehatd.Dodgeball.listener.CancelGriefListener;
import me.whitehatd.Dodgeball.listener.ItemClickListener;
import me.whitehatd.Dodgeball.listener.PlayerJoinLeaveListener;
import me.whitehatd.Dodgeball.npc.NPCListener;
import me.whitehatd.Dodgeball.utils.menu.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ListenerRegistration {

    public ListenerRegistration(Core core) {
        List<ListenerBase> listeners = Arrays.asList(
                new SnowballListener(core),
                new CancelGriefListener(core),
                new ItemClickListener(core),
                new PlayerJoinLeaveListener(core),
                new NPCListener(core),
                new MenuListener(core)
        );

        listeners.forEach(listener -> {
            Bukkit.getPluginManager().registerEvents(listener, core);
        });
    }

}