package me.whitehatd.Dodgeball.utils.menu;

import me.whitehatd.Dodgeball.Core;
import me.whitehatd.Dodgeball.utils.listener.ListenerBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener extends ListenerBase {


    public MenuListener(Core core) {
        super(core);
    }

    @EventHandler
    public void onMenu(InventoryClickEvent e){

        //set cancelled if menu is opened, even if click is inside player inventory

        if(e.getClickedInventory() == null) return;

        if(e.getInventory().getHolder() == null ||
                !(e.getInventory().getHolder() instanceof Menu)) return;


        if(!(e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT)) return;
        if(e.getClick().isCreativeAction()) return;
        if(e.getClick().isShiftClick()) return;
        if(e.getClick().isKeyboardClick()) return;

        if(e.getClickedInventory().getHolder() == null ||
                !(e.getClickedInventory().getHolder() instanceof Menu)) return;

        e.setCancelled(true);

        Menu menu = (Menu) e.getInventory().getHolder();

        if(menu.isStickySlot(e.getSlot()))
            menu.getStickyAction(e.getSlot()).accept(e);
        else
            menu.getAction(e.getSlot()).accept(e);
    }
}
