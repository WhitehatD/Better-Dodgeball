package me.whitehatd.Dodgeball.utils.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;


public class Button {

    private final ItemStack item;
    private Consumer<InventoryClickEvent> action;

    public Button(ItemStack item, Consumer<InventoryClickEvent> action){
        this.item = item;
        this.action = action;
    }

    public ItemStack getItem() {
        return item;
    }

    public Consumer<InventoryClickEvent> getAction() {
        return action;
    }

    public static Button of(ItemStack item, Consumer<InventoryClickEvent> action){
        return new Button(item, action);
    }



}
