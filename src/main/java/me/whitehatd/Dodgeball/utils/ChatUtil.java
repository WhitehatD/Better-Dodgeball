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

}
