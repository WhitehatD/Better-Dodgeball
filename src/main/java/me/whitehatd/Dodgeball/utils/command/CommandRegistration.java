package me.whitehatd.Dodgeball.utils.command;

import me.whitehatd.Dodgeball.Core;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class CommandRegistration {


    public CommandRegistration(Core core) {

        Reflections reflections = new Reflections("me.whitehatd");
        Set<Class<? extends CommandBase>> classes = reflections.getSubTypesOf(CommandBase.class);

        classes.forEach(clazz -> {

            try {
                Constructor<?> constructor = clazz.getConstructor(Core.class);

                CommandBase command = (CommandBase) constructor.newInstance(core);

                core.getCommand(command.getCommandName()).setExecutor( command);
                core.getCommand(command.getCommandName()).setTabCompleter(command);
                
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

    }
}
