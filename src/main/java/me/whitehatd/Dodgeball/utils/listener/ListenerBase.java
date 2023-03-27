package me.whitehatd.Dodgeball.utils.listener;

import me.whitehatd.Dodgeball.Core;
import org.bukkit.event.Listener;

public abstract class ListenerBase implements Listener {

    public Core core;

    public ListenerBase(Core core){
        this.core = core;
    }
}
