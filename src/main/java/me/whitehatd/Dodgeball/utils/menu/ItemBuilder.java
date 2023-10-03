package me.whitehatd.Dodgeball.utils.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private ItemStack finalItem;
    private List<String> lore;

    public ItemBuilder(Material material){
        this.finalItem = new ItemStack(material);
    }

    public ItemBuilder(ItemStack itemStack){
        this.finalItem = itemStack;
    }

    public ItemBuilder setName(String name){
        ItemMeta meta = finalItem.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        finalItem.setItemMeta(meta);

        return this;
    }

    public ItemBuilder setLore(List<String> toSet){

        if(lore == null)
            lore = new ArrayList<>();

        lore.addAll(toSet
                .stream()
                .map(str -> ChatColor.translateAlternateColorCodes('&', str)).toList());


        return this;
    }


    public ItemStack build(){
        ItemMeta meta = finalItem.getItemMeta();
        meta.setLore(lore);
        finalItem.setItemMeta(meta);

        return finalItem;
    }

}
