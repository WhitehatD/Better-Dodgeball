package me.whitehatd.Dodgeball.utils.command;

import me.whitehatd.Dodgeball.Core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class CommandBase implements CommandExecutor, TabCompleter {

    public final Core core;
    private final String commandName;
    private final String permission;
    private boolean playerOnly;

    public CommandBase(String commandName, String permission, boolean playerOnly, Core core){

        this.commandName = commandName;
        this.permission = permission;
        this.playerOnly = playerOnly;
        this.core = core;


    }


    public String getCommandName() {
        return commandName;
    }

    public abstract void run(CommandSender sender, String[] args);

    public abstract List<String> tab(String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!command.getName().equalsIgnoreCase(commandName)) return true;

        if (!(sender instanceof Player player)) {
            if(playerOnly) {
                reply(sender, "&cThis command is player-only.");

                return true;
            }

            run(sender, args);

            return true;
        }

        if (!permission.equals("") && !player.hasPermission(permission)){
            reply(player, "&cYou don't have permission!");
            return true;

        }
        run(sender, args);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return tab(args);
    }


    public void reply(CommandSender sender, String... messages){
        for(String message : messages)
            core.getChatUtil().message(sender, message);
    }
}