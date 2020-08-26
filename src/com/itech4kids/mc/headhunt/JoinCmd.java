package com.itech4kids.mc.headhunt;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class JoinCmd implements CommandExecutor {
    HeadHunt headhunt;

    public JoinCmd(HeadHunt headhunt) {
        this.headhunt = headhunt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ActivePlayer activePlayer = new ActivePlayer((Player) sender);
            ItemStack selectkits = new ItemStack(Material.BOW);
            ItemMeta itemMeta = selectkits.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GOLD + "Kit selector" + ChatColor.GRAY + " (Right Click)");
            selectkits.setItemMeta(itemMeta);
            headhunt.addPlayer(activePlayer);
            activePlayer.saveItems(activePlayer.getBukkitPlayer().getInventory().getContents());
            activePlayer.getBukkitPlayer().getInventory().clear();
            activePlayer.getBukkitPlayer().getInventory().setArmorContents(null);
            activePlayer.getBukkitPlayer().getInventory().addItem(new ItemStack(selectkits));
            return true;
        }
        return false;
    }
}