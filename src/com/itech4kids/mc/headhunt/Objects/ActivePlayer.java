package com.itech4kids.mc.headhunt.Objects;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class ActivePlayer {

    private class StartKits {
        Material material;
        int count;
        Enchantment enchantment;
        Boolean breakable;
        int enchantmentLevel;
        String name = null;
        String lore = null;
        PotionEffect potionEffect;


        private StartKits(Material material, int count,
                          String name, PotionEffectType potionEffectType,
                          int duration, int amplifier,
                          String lore, Boolean unbreakable) {
            this.material = material;
            this.count = count;
            this.enchantment = null;
            this.enchantmentLevel = 0;
            this.potionEffect = new PotionEffect(potionEffectType, duration, amplifier);
            this.name = name;
            this.lore = lore;
            this.breakable = unbreakable;
        }
        private StartKits(Material material, int count, Enchantment enchantment, int enchantmentLevel, String name) {
            this.material = material;
            this.count = count;
            this.enchantment = null;
            this.enchantmentLevel = 0;
            this.potionEffect = null;
            this.name = name;
            this.enchantment = enchantment;
            this.enchantmentLevel = enchantmentLevel;
            this.breakable = null;

        }
        private StartKits(Material material, int count, Enchantment enchantment, int enchantmentLevel, String name, boolean b) {
            this.material = material;
            this.count = count;
            this.enchantment = null;
            this.enchantmentLevel = 0;
            this.potionEffect = null;
            this.name = name;
            this.enchantment = enchantment;
            this.enchantmentLevel = enchantmentLevel;
            this.breakable = b;

        }
        private StartKits(Material material, int count) {
            this.material = material;
            this.count = count;
            this.enchantment = null;
            this.enchantmentLevel = 0;
            this.potionEffect = null;
            this.breakable = null;

        }
        private StartKits(Material material, int count, boolean b) {
            this.material = material;
            this.count = count;
            this.enchantment = null;
            this.enchantmentLevel = 0;
            this.potionEffect = null;
            this.breakable = b;

        }
        private StartKits(Material material, int count, Enchantment enchantment, int enchantmentLevel) {
            this.material = material;
            this.count = count;
            this.enchantment = enchantment;
            this.enchantmentLevel = enchantmentLevel;
            this.potionEffect = null;
            this.breakable = null;

        }
        private StartKits(Material material, int count, Enchantment enchantment, int enchantmentLevel, boolean b) {
            this.material = material;
            this.count = count;
            this.enchantment = enchantment;
            this.enchantmentLevel = enchantmentLevel;
            this.potionEffect = null;
            this.breakable = b;

        }
    }

    private Player player;
    public boolean isOp;
    public boolean isInGame;
    private ItemStack[] savedItems;
    public int wins;
    public int kills;
    public int coins;
    public boolean isVanished;
    private StartKits[] defaultKit = {
            new StartKits(Material.DIAMOND_SWORD, 1, true),
            new StartKits(Material.FISHING_ROD, 1, true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 8),
    };

    private StartKits[] archerKit = {
            new StartKits(Material.GOLD_AXE, 1, true),
            new StartKits(Material.BOW, 1, Enchantment.ARROW_DAMAGE, 6, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 4),
            new StartKits(Material.CHAINMAIL_HELMET, 1, true),
            new StartKits(Material.CHAINMAIL_CHESTPLATE, 1, true),
            new StartKits(Material.CHAINMAIL_LEGGINGS, 1, true),
            new StartKits(Material.CHAINMAIL_BOOTS, 1, true),
            new StartKits(Material.ARROW, 400),
    };
    private StartKits[] berserkerKit = {
            new StartKits(Material.DIAMOND_SWORD, 1, Enchantment.DAMAGE_ALL, 3, ChatColor.RED + "Berserkers Sword", true),
            new StartKits(Material.GOLD_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.GOLD_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 8),
    };
    private StartKits[] endermanKit = {
            new StartKits(Material.DIAMOND_SWORD, 1, true ),
            new StartKits(Material.CHAINMAIL_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.CHAINMAIL_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.ENDER_PEARL, 4),
            new StartKits(Material.GOLDEN_APPLE, 2),
    };
    private StartKits[] pigmanKit = {
            new StartKits(Material.GOLD_SWORD, 1,  Enchantment.DAMAGE_ALL, 3, ChatColor.GOLD + "Pigman Sword", true),
            new StartKits(Material.LEATHER_HELMET, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, true),
            new StartKits(Material.LEATHER_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, true),
            new StartKits(Material.LEATHER_LEGGINGS, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, true),
            new StartKits(Material.LEATHER_BOOTS, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 8),
    };
    private StartKits[] herobrineKit = {
            new StartKits(Material.DIAMOND_SWORD, 1,  Enchantment.DAMAGE_ALL, 1, ChatColor.BLUE + "Herobrine's Sword", true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
    };
    private StartKits[] tankKit = {
            new StartKits(Material.WOOD_SWORD, 1, true),
            new StartKits(Material.DIAMOND_HELMET, 1, true),
            new StartKits(Material.DIAMOND_CHESTPLATE, 1, true),
            new StartKits(Material.DIAMOND_LEGGINGS, 1, true),
            new StartKits(Material.DIAMOND_BOOTS, 1, true),
            new StartKits(Material.GOLDEN_CARROT, 64),
            new StartKits(Material.GOLDEN_APPLE, 10),
    };
    private StartKits[] pyromancerKit = {
            new StartKits(Material.DIAMOND_SWORD, 1, Enchantment.FIRE_ASPECT, 2, ChatColor.RED + "Pyromancer's Sword", true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 8),
    };
    private StartKits[] fisherManKit = {
            new StartKits(Material.FISHING_ROD, 1, Enchantment.DAMAGE_ALL, 5, true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 8),
    };
    private StartKits[] scoutKit = {
            new StartKits(Material.STONE_SWORD, 1, Enchantment.DAMAGE_ALL, 1, true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.DIAMOND_BOOTS, 1, Enchantment.PROTECTION_FALL, 5, ChatColor.AQUA + "Hermes' Boots", true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 5),
    };
    private StartKits[] farmerKit = {
            new StartKits(Material.DIAMOND_HOE, 1, Enchantment.DAMAGE_ALL, 5, true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.EGG, 64),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 8),
    };
    private StartKits[] slothKit = {
            new StartKits(Material.GHAST_TEAR, 1, Enchantment.DAMAGE_ALL, 6, true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.FISHING_ROD, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 8),
    };
    private StartKits[] pigriderKit = {
            new StartKits(Material.GOLD_SWORD, 1, Enchantment.DAMAGE_ALL, 2, true),
            new StartKits(Material.GOLD_HELMET, 1, Enchantment.PROTECTION_ENVIRONMENTAL,2, true),
            new StartKits(Material.GOLD_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL,2, true),
            new StartKits(Material.GOLD_LEGGINGS, 1, Enchantment.PROTECTION_ENVIRONMENTAL,2, true),
            new StartKits(Material.GOLD_BOOTS, 1, Enchantment.PROTECTION_ENVIRONMENTAL,2, true),
            new StartKits(Material.FISHING_ROD, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 8),
    };
    private StartKits[] trollKit = {
            new StartKits(Material.IRON_SWORD, 1, Enchantment.DAMAGE_ALL, 1, true),
            new StartKits(Material.STICK, 1, Enchantment.KNOCKBACK, 3, true),
            new StartKits(Material.IRON_HELMET, 1, Enchantment.THORNS, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, Enchantment.THORNS, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, Enchantment.THORNS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, Enchantment.THORNS, 1, true),
            new StartKits(Material.SNOW_BALL, 64),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 4),
    };
    private StartKits[] onepoundfishKit = {
            new StartKits(Material.RAW_FISH, 1, Enchantment.DAMAGE_ALL, 5, ChatColor.AQUA + "The One-Pound-Fish", true),
            new StartKits(Material.FISHING_ROD, 1, true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 4),
    };
    private StartKits[] zombieKit = {
            new StartKits(Material.GOLD_SWORD, 1, Enchantment.DAMAGE_ALL, 2, ChatColor.GOLD + "Zombie Sword", true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 4),
    };
    private StartKits[] dreadlordKit = {
            new StartKits(Material.IRON_SWORD, 1, Enchantment.DAMAGE_ALL, 1, ChatColor.DARK_GRAY + "Dreadlord Sword", true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 4),
    };
    private StartKits[] bomberKit = {
            new StartKits(Material.IRON_SWORD, 1, Enchantment.DAMAGE_ALL, 1, ChatColor.DARK_GRAY + "Bomber's Sword", true),
            new StartKits(Material.BOW, 1, Enchantment.ARROW_DAMAGE, 1, ChatColor.RED + "Explosive Bow", true),
            new StartKits(Material.ARROW, 32),
            new StartKits(Material.TNT, 15),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 4),
    };
    private StartKits[] meatKit = {
            new StartKits(Material.COOKED_BEEF, 1, Enchantment.DAMAGE_ALL, 6, true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 4),
    };
    private StartKits[] snowmanKit = {
            new StartKits(Material.DIAMOND_SPADE, 1, Enchantment.DAMAGE_ALL, 4, true),
            new StartKits(Material.SNOW_BALL, 64),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 5),
    };
    private StartKits[] spiderKit = {
            new StartKits(Material.IRON_SWORD, 1, Enchantment.DAMAGE_ALL, 1, ChatColor.GRAY + "Spider Sword", true),
            new StartKits(Material.WEB, 12),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, Enchantment.PROTECTION_FALL, 10, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 3),
    };
    private StartKits[] speleoKit = {
            new StartKits(Material.DIAMOND_PICKAXE, 1, Enchantment.DAMAGE_ALL, 3, true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.DIAMOND_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.LEATHER_BOOTS, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 2, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 6),
    };
    private StartKits[] ecologistKit = {
            new StartKits(Material.DIAMOND_AXE, 1, Enchantment.DAMAGE_ALL, 2, true),
            new StartKits(Material.FISHING_ROD, 1, true),
            new StartKits(Material.IRON_HELMET, 1, true),
            new StartKits(Material.IRON_CHESTPLATE, 1, true),
            new StartKits(Material.IRON_LEGGINGS, 1, true),
            new StartKits(Material.IRON_BOOTS, 1, true),
            new StartKits(Material.COOKED_BEEF, 64),
            new StartKits(Material.GOLDEN_APPLE, 6),
    };
    private StartKits[] startKits = defaultKit;

    public ActivePlayer(Player player) {
        this.player = player;
        coins = Config.getCoins(player.getName());
        kills = Config.getKills(player.getName());
        wins = Config.getWins(player.getName());
        this.isInGame = false;
        isVanished = false;
    }

    public void TelePortTo(Location location) {
        player.teleport(location);
    }

    public Player getBukkitPlayer() {
        return player;
    }
    public int getHeadCount() {
        // count the heads in the inventory
        int headCount = 0;
        for (ItemStack i : player.getInventory().getContents()) {
            if ((i != null) && (i.getType() == Material.SKULL_ITEM)) {
                headCount += i.getAmount();
            }
        }
        return headCount;
    }

    public void saveItems(ItemStack[] savedItems) {
        this.savedItems = savedItems;
    }

    public void giveStartKits() {
        for (int i = 0; i < startKits.length; i ++) {
            //for (int j = 0; j < startKits[i].count; j ++)
            {
                ItemStack item;
                if (startKits[i].material ==  Material.POTION){
                    Potion splash = new Potion(PotionType.getByEffect(startKits[i].potionEffect.getType()), 0);
                    splash.setSplash(true);

                    item = splash.toItemStack(startKits[i].count);
                } else {
                    item = new ItemStack(startKits[i].material, startKits[i].count);
                }
                ItemMeta itemMeta = item.getItemMeta();

                if (startKits[i].breakable != null) {
                    itemMeta.spigot().setUnbreakable(true);
                }

                if (startKits[i].name != null){
                    itemMeta.setDisplayName(startKits[i].name);
                }
                if (startKits[i].enchantment != null){
                    itemMeta.addEnchant(startKits[i].enchantment, startKits[i].enchantmentLevel, true);
                }
                if (startKits[i].potionEffect != null){
                    PotionMeta potionMeta = (PotionMeta) itemMeta;
                    potionMeta.addCustomEffect(startKits[i].potionEffect, true);

                }
                if (startKits[i].lore != null) {
                    List<String> lore = new ArrayList<String>();
                    lore.add(startKits[i].lore);
                    itemMeta.setLore(lore);
                }
                item.setItemMeta(itemMeta);
                player.getInventory().addItem(item);
            }
        }
    }

    public void restoreItems() {
        player.getInventory().clear();
        for (int i = 0; i < savedItems.length; i ++) {
            if (savedItems[i] != null) {
                player.getInventory().clear();
                player.getInventory().addItem(savedItems[i]);
            }
        }
    }

    public void addCoins(int count) {
        coins += count;
    }

    public boolean hasEnoughCoins(int count) {
        if (coins > count) {
            return true;
        } else {
            return false;
        }
    }

    public boolean decCoins(int count) {
        if (hasEnoughCoins(count)) {
            coins -= count;
            return true;
        } else {
            return false;
        }
    }

    public void selectKit(KitsType type){
        if (type == KitsType.ENDERMAN){
            startKits = endermanKit;
        }else if (type == KitsType.ARCHER){
            startKits = archerKit;
        }else  if(type == KitsType.BERSERKER){
            startKits = berserkerKit;
        }else if (type == KitsType.DEFAULT){
            startKits = defaultKit;
        }else if (type == KitsType.PIGMAN){
            startKits = pigmanKit;
        }else if(type == KitsType.HEROBRINE){
            startKits = herobrineKit;
        }else if (type == KitsType.TANK){
            startKits = tankKit;
        }else if (type == KitsType.PYROMANCER){
            startKits = pyromancerKit;
        }else if (type == KitsType.FISHERMAN){
            startKits = fisherManKit;
        }else if (type == KitsType.SCOUT){
            startKits = scoutKit;
        }else if (type == KitsType.FARMER){
            startKits = farmerKit;
        }else if(type == KitsType.SLOTH){
            startKits = slothKit;
        }else if(type == KitsType.PIGRIDER){
            startKits = pigriderKit;
        }else if(type == KitsType.TROLL){
            startKits = trollKit;
        }else if (type == KitsType.ONEPOUNDFISH){
            startKits = onepoundfishKit;
        }else if(type == KitsType.ZOMBIE){
            startKits = zombieKit;
        }else if(type == KitsType.DREADLORD){
            startKits = dreadlordKit;
        }else if(type == KitsType.BOMBER){
            startKits = bomberKit;
        }else if(type == KitsType.MEATMASTER){
            startKits = meatKit;
        }else if(type == KitsType.SNOWMAN){
            startKits = snowmanKit;
        }else if(type == KitsType.SPELEOLOGIST){
            startKits = speleoKit;
        }else if(type == KitsType.SPIDER){
            startKits = spiderKit;
        }else if(type == KitsType.ECOLOGIST){
            startKits = ecologistKit;
        }
    }

}
