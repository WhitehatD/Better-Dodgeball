package me.whitehatd.Dodgeball.utils.listener;

import me.whitehatd.Dodgeball.Core;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class ListenerRegistration {

    public ListenerRegistration(Core core) {

        Reflections reflections = new Reflections("me.whitehatd");

        Set<Class<? extends ListenerBase>> classes = reflections.getSubTypesOf(ListenerBase.class);

        for (Class<? extends ListenerBase> clazz : classes) {

            try {

                Constructor<?> constructor = clazz.getConstructor(Core.class);

                Bukkit.getPluginManager().registerEvents((Listener) constructor.newInstance(core), core);

            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}