package com.itech4kids.mc.headhunt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopCmd implements CommandExecutor {
    private HeadHunt headhunt;
    private ArrayList<ItemStack> shopItems;
    Inventory shop;

    public ShopCmd(HeadHunt headhunt) {
        this.headhunt = headhunt;
        initializeShop();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ActivePlayer activePlayer = new ActivePlayer((Player) sender);
            activePlayer.getBukkitPlayer().openInventory(shop);
            return true;
        }
        return false;
    }

    private void initializeShop() {
        shopItems = new ArrayList<ItemStack>();

        shopItems.add(new ItemStack(Material.ANVIL));
        shopItems.add(new ItemStack(Material.GOLDEN_APPLE));
        shopItems.add(new ItemStack(Material.DIAMOND_SWORD));

        shop = Bukkit.createInventory(null, 27, "HeadHunt Shop");
        for (ItemStack item: shopItems) {
            shop.setItem(shop.firstEmpty(), item);
        }

    }

}
