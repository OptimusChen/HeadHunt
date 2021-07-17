package com.itech4kids.mc.headhunt.Commands;

import com.itech4kids.mc.headhunt.HeadHunt;
import com.itech4kids.mc.headhunt.Objects.ActivePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Material.*;

public class ShopCmd implements CommandExecutor {
    private HeadHunt headhunt;
    Inventory shop;

    public ShopCmd(HeadHunt headhunt) {
        this.headhunt = headhunt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ActivePlayer activePlayer = new ActivePlayer((Player) sender);
            shop = Bukkit.createInventory(null, 27, "HeadHunt Shop");
            shop.clear();

            ItemStack space1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData());
            ItemMeta spaceMeta = space1.getItemMeta();
            spaceMeta.setDisplayName(" ");
            space1.setItemMeta(spaceMeta);

            ItemStack kits = new ItemStack(DIAMOND_SWORD, 1);
            ItemMeta kitsMeta = kits.getItemMeta();
            List<String> kitsLore = new ArrayList<>();
            kitsMeta.setDisplayName(ChatColor.RED + "HeadHunt Kits");
            kitsLore.add(ChatColor.GRAY + "View and Purchase a");
            kitsLore.add(ChatColor.GRAY + "selection of unique");
            kitsLore.add(ChatColor.GRAY + "Kits for HeadHunt");
            kitsLore.add(ChatColor.GRAY + " ");
            kitsLore.add(ChatColor.YELLOW + "Click to view!");
            kitsMeta.setLore(kitsLore);
            kits.setItemMeta(kitsMeta);

            ItemStack perks = new ItemStack(CAULDRON_ITEM, 1);
            ItemMeta perksMeta = perks.getItemMeta();
            List<String> perksLore = new ArrayList<>();
            perksMeta.setDisplayName(ChatColor.GREEN + "HeadHunt Perks");
            perksLore.add(ChatColor.GRAY + "View and Purchase a");
            perksLore.add(ChatColor.GRAY + "selection of unique");
            perksLore.add(ChatColor.GRAY + "Perks for HeadHunt");
            perksLore.add(ChatColor.GRAY + " ");
            perksLore.add(ChatColor.YELLOW + "Click to view!");
            perksMeta.setLore(perksLore);
            perks.setItemMeta(perksMeta);

            ItemStack panel = new ItemStack(FIREBALL, 1);
            ItemMeta panelMeta = perks.getItemMeta();
            List<String> panelLore = new ArrayList<>();
            panelMeta.setDisplayName(ChatColor.GREEN + "HeadHunt Panel Abilities");
            panelLore.add(ChatColor.GRAY + "View and Purchase a");
            panelLore.add(ChatColor.GRAY + "selection of unique");
            panelLore.add(ChatColor.GRAY + "Panel Abilities for ");
            panelLore.add(ChatColor.GRAY + "HeadHunt!");
            panelLore.add(ChatColor.GRAY + " ");
            panelLore.add(ChatColor.YELLOW + "Click to view!");
            panelMeta.setLore(panelLore);
            panel.setItemMeta(panelMeta);

            for (int i = 0; i < 27; ++i) {
                shop.setItem(i, space1);
            }

            shop.setItem(11, kits);
            shop.setItem(13, panel);
            shop.setItem(15, perks);
            activePlayer.getBukkitPlayer().openInventory(shop);
            return true;
        }
        return false;
    }
}
