package com.itech4kids.mc.headhunt.Util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemHandler {

    public static ItemStack createKitSelectionItem(String kitName, ChatColor color, Material mat, KitsManager.StartKits[] items){
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setDisplayName(color + kitName + " Kit " + ChatColor.YELLOW + "(Click to Select!)");

        for (KitsManager.StartKits startKits : items){
            ItemStack itemStack = startKits.toItemStack();
            if (item.getItemMeta().hasDisplayName()){
                lore.add(itemStack.getItemMeta().getDisplayName());
            }else{
                String matName = WordUtils.capitalize(itemStack.getType().name().toLowerCase().replaceAll("_", " ")
                        + ChatColor.DARK_GRAY) + " x" + itemStack.getAmount();
                lore.add(ChatColor.GRAY +
                        matName);
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

}
