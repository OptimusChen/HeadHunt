package com.itech4kids.mc.headhunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class KitCmd implements CommandExecutor {
    private HeadHunt headhunt;
    private ArrayList<ItemStack> kitsItems;
    Inventory kits;

    public KitCmd(HeadHunt headhunt) {
        this.headhunt = headhunt;
        initializeKits();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ActivePlayer activePlayer = new ActivePlayer((Player) sender);
            activePlayer.getBukkitPlayer().openInventory(kits);
            return true;
        }
        return false;
    }

    private void initializeKits() {
        kitsItems = new ArrayList<ItemStack>();
        ItemStack enderpearl = new ItemStack(Material.ENDER_PEARL);
        ItemStack bow = new ItemStack(Material.BOW);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack normal = new ItemStack(Material.IRON_CHESTPLATE);
        ItemStack pigman = new ItemStack(Material.GRILLED_PORK);
        ItemStack tank = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemStack pyromancer = new ItemStack(Material.FLINT_AND_STEEL);
        ItemStack fisherman = new ItemStack(Material.FISHING_ROD);
        ItemStack herobrine = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        ItemStack scout = new ItemStack(Material.DIAMOND_BOOTS);
        ItemStack farmer = new ItemStack(Material.EGG);
        ItemStack sloth = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        ItemStack pigrider = new ItemStack(Material.CARROT_STICK);
        ItemStack troll = new ItemStack(Material.WEB);
        ItemStack onepoundfish = new ItemStack(Material.RAW_FISH);
        ItemStack zombie = new ItemStack(Material.ROTTEN_FLESH);
        ItemStack dreadlord = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        ItemStack bomber = new ItemStack(Material.TNT);

        ItemMeta itemMeta = enderpearl.getItemMeta();
        ItemMeta itemMeta1 = bow.getItemMeta();
        ItemMeta itemMeta2 = sword.getItemMeta();
        ItemMeta itemMeta3 = normal.getItemMeta();
        ItemMeta itemMeta4 = pigman.getItemMeta();
        ItemMeta itemMeta9 = scout.getItemMeta();
        ItemMeta itemMeta10 = farmer.getItemMeta();
        SkullMeta itemMeta5 = (SkullMeta) herobrine.getItemMeta();
        ItemMeta itemMeta6 = tank.getItemMeta();
        ItemMeta itemMeta7 = pyromancer.getItemMeta();
        ItemMeta itemMeta8 = fisherman.getItemMeta();
        SkullMeta itemMeta11 = (SkullMeta) sloth.getItemMeta();
        ItemMeta itemMeta12 = pigrider.getItemMeta();
        ItemMeta itemMeta13 = troll.getItemMeta();
        ItemMeta itemMeta14 = onepoundfish.getItemMeta();
        ItemMeta itemMeta15 = zombie.getItemMeta();
        SkullMeta itemMeta16 = (SkullMeta) dreadlord.getItemMeta();
        ItemMeta itemMeta17 = bomber.getItemMeta();

        itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Enderman Kit");
        itemMeta1.setDisplayName(ChatColor.YELLOW + "Archer Kit");
        itemMeta2.setDisplayName(ChatColor.RED + "Berserker Kit");
        itemMeta3.setDisplayName(ChatColor.WHITE + "Default Kit");
        itemMeta4.setDisplayName(ChatColor.GOLD + "Pigman Kit");
        itemMeta5.setDisplayName(ChatColor.DARK_PURPLE + "Herobrine Kit");
        itemMeta4.setDisplayName(ChatColor.GOLD + "Pigman Kit");
        itemMeta6.setDisplayName(ChatColor.DARK_GRAY + "Tank Kit");
        itemMeta7.setDisplayName(ChatColor.RED + "Pyromancer Kit");
        itemMeta8.setDisplayName(ChatColor.AQUA + "Fisherman Kit");
        itemMeta9.setDisplayName(ChatColor.BLUE + "Scout Kit");
        itemMeta10.setDisplayName(ChatColor.GREEN + "Farmer Kit");
        itemMeta11.setDisplayName(ChatColor.GRAY + "Sloth Kit");
        itemMeta12.setDisplayName(ChatColor.GOLD + "Pig Rider Kit");
        itemMeta13.setDisplayName(ChatColor.LIGHT_PURPLE + "Troll Kit");
        itemMeta14.setDisplayName(ChatColor.AQUA + "One-Pound-Fish Kit");
        itemMeta15.setDisplayName(ChatColor.DARK_GREEN + "Zombie Kit");
        itemMeta16.setDisplayName(ChatColor.DARK_GRAY + "Dreadlord Kit");
        itemMeta17.setDisplayName(ChatColor.DARK_RED + "Bomber Kit");
        itemMeta5.setOwner("01Herobrine10");
        itemMeta11.setOwner("Sloth");
        itemMeta16.setOwner("WitherSkeleton");

        tank.setItemMeta(itemMeta6);
        pyromancer.setItemMeta(itemMeta7);
        fisherman.setItemMeta(itemMeta8);
        normal.setItemMeta(itemMeta3);
        enderpearl.setItemMeta(itemMeta);
        bow.setItemMeta(itemMeta1);
        sword.setItemMeta(itemMeta2);
        pigman.setItemMeta(itemMeta4);
        herobrine.setItemMeta(itemMeta5);
        farmer.setItemMeta(itemMeta10);
        scout.setItemMeta(itemMeta9);
        sloth.setItemMeta(itemMeta11);
        pigrider.setItemMeta(itemMeta12);
        troll.setItemMeta(itemMeta13);
        onepoundfish.setItemMeta(itemMeta14);
        zombie.setItemMeta(itemMeta15);
        dreadlord.setItemMeta(itemMeta16);
        bomber.setItemMeta(itemMeta17);

        kitsItems.add(new ItemStack(normal));
        kitsItems.add(new ItemStack(enderpearl));
        kitsItems.add(new ItemStack(bow));
        kitsItems.add(new ItemStack(sword));
        kitsItems.add(new ItemStack(pigman));
        kitsItems.add(new ItemStack(herobrine));
        kitsItems.add(new ItemStack(tank));
        kitsItems.add(new ItemStack(pyromancer));
        kitsItems.add(new ItemStack(fisherman));
        kitsItems.add(new ItemStack(scout));
        kitsItems.add(new ItemStack(farmer));
        kitsItems.add(new ItemStack(sloth));
        kitsItems.add(new ItemStack(pigrider));
        kitsItems.add(new ItemStack(troll));
        kitsItems.add(new ItemStack(onepoundfish));
        kitsItems.add(new ItemStack(zombie));
        kitsItems.add(new ItemStack(dreadlord));
        kitsItems.add(new ItemStack(bomber));

        kits = Bukkit.createInventory(null, 27, "HeadHunt Kits");
        for (ItemStack item: kitsItems) {
            kits.setItem(kits.firstEmpty(), item);
        }

    }

}
