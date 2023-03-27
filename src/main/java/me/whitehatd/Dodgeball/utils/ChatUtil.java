package me.whitehatd.Dodgeball.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatUtil {

    public String toColor(String base){
        return ChatColor.translateAlternateColorCodes('&', base);
    }

    public void message(CommandSender player, String string){
        player.sendMessage(toColor(string));
    }

    public String capitalize(String input){
        String copy = input;

        copy = String.valueOf(copy.charAt(0)).toUpperCase() + copy.substring(1).toLowerCase();

        copy = copy.replaceAll("_", " ");

        return copy;
    }

    public void message(CommandSender player, List<String> messages){
        for(String message : messages)
            message(player, message);
    }

    public void sendTutorialChat(Player player){
        message(player, List.of(
                "&d&m                                                       ",
                "&6&lLeft Click &e- simple attack &c1HP",
                "&6&lLeft Click x3 &7(without being touched) &e- combo attack &c3HP",
                "&6&lShift + Left Click &7(5 sec delay) &e- right hand uppercut &c3HP",
                "&7",
                "&6&lRight Click &7(5 sec delay) &e- left hook &c2HP",
                "&d&m                                                       "

        ));
    }

    public void sendActionBar(Player player, String text){

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(
                        toColor(
                                text)));

    }

    public void sendTitle(Player player, String title, String subtitle){

        player.sendTitle(toColor(title), toColor(subtitle));

    }
}
