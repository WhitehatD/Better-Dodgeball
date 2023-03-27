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

    public ItemBuilder damage(short damage){

        Damageable meta = (Damageable) finalItem.getItemMeta();
        meta.setDamage(damage);
        finalItem.setItemMeta((ItemMeta) meta);

        return this;
    }

    public ItemBuilder unbreakable(boolean bool){

        ItemMeta meta = finalItem.getItemMeta();
        meta.setUnbreakable(bool);
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

    public ItemBuilder setCustomModelData(int data){

        ItemMeta meta = finalItem.getItemMeta();
        meta.setCustomModelData(data);
        finalItem.setItemMeta(meta);

        return this;

    }

    public ItemBuilder hideAttributes(){
        ItemMeta meta = finalItem.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        finalItem.setItemMeta(meta);

        return this;
    }

    public ItemBuilder addLore(String string){

        if(lore == null)
            lore = new ArrayList<>();

        lore.add(ChatColor.translateAlternateColorCodes('&', string));

        return this;

    }

    public ItemBuilder addLore(List<String> list){

        if(lore == null)
            lore = new ArrayList<>();

        list.forEach(this::addLore);

        return this;
    }

    public ItemStack build(){
        ItemMeta meta = finalItem.getItemMeta();
        meta.setLore(lore);
        finalItem.setItemMeta(meta);

        return finalItem;
    }

}
