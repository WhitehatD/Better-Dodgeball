package me.whitehatd.Dodgeball.utils.menu;

import me.whitehatd.Dodgeball.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Menu implements InventoryHolder {

    private HashMap<Integer, Button> actions;
    private HashMap<Integer, Button> sticky;

    private final int size;
    private final String name;

    private int currentPage;
    private boolean isAutoPaginated;

    public final Core core;

    public Menu(Core core, int size, String name){


        this.core = core;
        this.size = size;
        this.name = name;

        actions = new HashMap<>();
        sticky = new HashMap<>();

        isAutoPaginated = false;
        currentPage = 0;

    }

    public Menu setButton(int page, int slot, Button button){
        actions.put(page * size + slot, button);

        return this;
    }

    private Menu setButtonRaw(int slot, Button button){
        actions.put(slot, button);

        return this;
    }

    public Menu addAll(int fromSlot, List<Button> buttons){
        int counter = fromSlot;

        for(Button button : buttons){

            setButtonRaw(counter, button);

            if((counter / size + 1) * size - 10 == counter)
                counter += 10;
            else counter ++;
        }

        return this;
    }

    public Menu setStickyButton(int slot, Button button){

        sticky.put(slot, button);

        return this;
    }

    public boolean isStickySlot(int slot){
        return sticky.get(slot) != null;
    }

    public Consumer<InventoryClickEvent> getStickyAction(int slot){
        return sticky.get(slot).getAction();
    }

    public Menu autoPaginate() {

        isAutoPaginated = true;

        setStickyButton(size - 4,
                Button.of(
                        new ItemBuilder(Material.ARROW)
                                .setName("&6Next page")
                                .setLore(Collections.singletonList("&7Click for the next page"))
                                .build(),
                        e -> {

                            if (actions.size() <= (currentPage + 1) * size - 9)
                                return;

                            currentPage++;

                            Bukkit.getScheduler().runTaskLater(core, () -> {
                                e.getWhoClicked().openInventory(getInventory());
                            }, 2L);

                        }
                ));

        setStickyButton(size - 6,
                Button.of(
                        new ItemBuilder(Material.ARROW)
                                .setName("&6Previous page")
                                .setLore(Collections.singletonList("&7Click for the previous page"))
                                .build(),
                        e -> {

                            if (currentPage == 0)
                                return;

                            currentPage--;

                            Bukkit.getScheduler().runTaskLater(core, () -> {
                                e.getWhoClicked().openInventory(getInventory());
                            }, 2L);

                        }
                ));


        return this;
    }


    public Consumer<InventoryClickEvent> getAction(int slot){

        //if slot is empty / outside of inventory do nothing

        if(actions.get(slot) == null)
            return inv -> {};

        return actions.get(slot).getAction();
    }


    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, size, ChatColor.translateAlternateColorCodes('&', name));

        if(isAutoPaginated)
            autoPaginate();

        for(Integer slot : sticky.keySet())
            inventory.setItem(slot, sticky.get(slot).getItem());

        for(Integer slot : actions.keySet())
            if(slot >= currentPage * size && slot < (currentPage + 1) * size)
                inventory.setItem(slot - currentPage * size, actions.get(slot).getItem());

        return inventory;
    }

}
