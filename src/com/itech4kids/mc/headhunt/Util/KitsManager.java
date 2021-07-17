package com.itech4kids.mc.headhunt.Util;

import com.itech4kids.mc.headhunt.Objects.ActivePlayer;
import org.bukkit.ChatColor;
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

public class KitsManager {

    public static class StartKits {
        Material material;
        int count;
        Enchantment enchantment;
        Boolean breakable;
        int enchantmentLevel;
        String name = null;
        String lore = null;
        PotionEffect potionEffect;


        public StartKits(Material material, int count,
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
        public StartKits(Material material, int count, Enchantment enchantment, int enchantmentLevel, String name) {
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
        public StartKits(Material material, int count, Enchantment enchantment, int enchantmentLevel, String name, boolean b) {
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
        public StartKits(Material material, int count) {
            this.material = material;
            this.count = count;
            this.enchantment = null;
            this.enchantmentLevel = 0;
            this.potionEffect = null;
            this.breakable = null;

        }
        public StartKits(Material material, int count, boolean b) {
            this.material = material;
            this.count = count;
            this.enchantment = null;
            this.enchantmentLevel = 0;
            this.potionEffect = null;
            this.breakable = b;

        }
        public StartKits(Material material, int count, Enchantment enchantment, int enchantmentLevel) {
            this.material = material;
            this.count = count;
            this.enchantment = enchantment;
            this.enchantmentLevel = enchantmentLevel;
            this.potionEffect = null;
            this.breakable = null;

        }
        public StartKits(Material material, int count, Enchantment enchantment, int enchantmentLevel, boolean b) {
            this.material = material;
            this.count = count;
            this.enchantment = enchantment;
            this.enchantmentLevel = enchantmentLevel;
            this.potionEffect = null;
            this.breakable = b;

        }

        public ItemStack toItemStack(){
            ItemStack item;
            if (material ==  Material.POTION){
                Potion splash = new Potion(PotionType.getByEffect(potionEffect.getType()), 0);
                splash.setSplash(true);

                item = splash.toItemStack(count);
            } else {
                item = new ItemStack(material, count);
            }
            ItemMeta itemMeta = item.getItemMeta();

            if (breakable != null) {
                itemMeta.spigot().setUnbreakable(true);
            }

            if (name != null){
                itemMeta.setDisplayName(name);
            }
            if (enchantment != null){
                itemMeta.addEnchant(enchantment, enchantmentLevel, true);
            }
            if (potionEffect != null){
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.addCustomEffect(potionEffect, true);

            }
            if (lore != null) {
                List<String> lore = new ArrayList<String>();
                lore.add(this.lore);
                itemMeta.setLore(lore);
            }
            item.setItemMeta(itemMeta);
            return item;
        }
    }

    public static KitsManager.StartKits[] defaultKit = {
            new KitsManager.StartKits(Material.DIAMOND_SWORD, 1, true),
            new KitsManager.StartKits(Material.FISHING_ROD, 1, true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 8),
    };

    public static KitsManager.StartKits[] archerKit = {
            new KitsManager.StartKits(Material.GOLD_AXE, 1, true),
            new KitsManager.StartKits(Material.BOW, 1, Enchantment.ARROW_DAMAGE, 6, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 4),
            new KitsManager.StartKits(Material.CHAINMAIL_HELMET, 1, true),
            new KitsManager.StartKits(Material.CHAINMAIL_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.CHAINMAIL_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.CHAINMAIL_BOOTS, 1, true),
            new KitsManager.StartKits(Material.ARROW, 400),
    };
    public static KitsManager.StartKits[] berserkerKit = {
            new KitsManager.StartKits(Material.DIAMOND_SWORD, 1, Enchantment.DAMAGE_ALL, 3, ChatColor.RED + "Berserkers Sword", true),
            new KitsManager.StartKits(Material.GOLD_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.GOLD_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 8),
    };
    public static KitsManager.StartKits[] endermanKit = {
            new KitsManager.StartKits(Material.DIAMOND_SWORD, 1, true ),
            new KitsManager.StartKits(Material.CHAINMAIL_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.CHAINMAIL_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.ENDER_PEARL, 4),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 2),
    };
    public static KitsManager.StartKits[] pigmanKit = {
            new KitsManager.StartKits(Material.GOLD_SWORD, 1,  Enchantment.DAMAGE_ALL, 3, ChatColor.GOLD + "Pigman Sword", true),
            new KitsManager.StartKits(Material.LEATHER_HELMET, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, true),
            new KitsManager.StartKits(Material.LEATHER_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, true),
            new KitsManager.StartKits(Material.LEATHER_LEGGINGS, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, true),
            new KitsManager.StartKits(Material.LEATHER_BOOTS, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 8),
    };
    public static KitsManager.StartKits[] herobrineKit = {
            new KitsManager.StartKits(Material.DIAMOND_SWORD, 1,  Enchantment.DAMAGE_ALL, 1, ChatColor.BLUE + "Herobrine's Sword", true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
    };
    public static KitsManager.StartKits[] tankKit = {
            new KitsManager.StartKits(Material.WOOD_SWORD, 1, true),
            new KitsManager.StartKits(Material.DIAMOND_HELMET, 1, true),
            new KitsManager.StartKits(Material.DIAMOND_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.DIAMOND_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.DIAMOND_BOOTS, 1, true),
            new KitsManager.StartKits(Material.GOLDEN_CARROT, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 10),
    };
    public static KitsManager.StartKits[] pyromancerKit = {
            new KitsManager.StartKits(Material.DIAMOND_SWORD, 1, Enchantment.FIRE_ASPECT, 2, ChatColor.RED + "Pyromancer's Sword", true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 8),
    };
    public static KitsManager.StartKits[] fisherManKit = {
            new KitsManager.StartKits(Material.FISHING_ROD, 1, Enchantment.DAMAGE_ALL, 5, true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 8),
    };
    public static KitsManager.StartKits[] scoutKit = {
            new KitsManager.StartKits(Material.STONE_SWORD, 1, Enchantment.DAMAGE_ALL, 1, true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.DIAMOND_BOOTS, 1, Enchantment.PROTECTION_FALL, 5, ChatColor.AQUA + "Hermes' Boots", true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 5),
    };
    public static KitsManager.StartKits[] farmerKit = {
            new KitsManager.StartKits(Material.DIAMOND_HOE, 1, Enchantment.DAMAGE_ALL, 5, true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.EGG, 64),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 8),
    };
    public static KitsManager.StartKits[] slothKit = {
            new KitsManager.StartKits(Material.GHAST_TEAR, 1, Enchantment.DAMAGE_ALL, 6, true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.FISHING_ROD, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 8),
    };
    public static KitsManager.StartKits[] pigriderKit = {
            new KitsManager.StartKits(Material.GOLD_SWORD, 1, Enchantment.DAMAGE_ALL, 2, true),
            new KitsManager.StartKits(Material.GOLD_HELMET, 1, Enchantment.PROTECTION_ENVIRONMENTAL,2, true),
            new KitsManager.StartKits(Material.GOLD_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL,2, true),
            new KitsManager.StartKits(Material.GOLD_LEGGINGS, 1, Enchantment.PROTECTION_ENVIRONMENTAL,2, true),
            new KitsManager.StartKits(Material.GOLD_BOOTS, 1, Enchantment.PROTECTION_ENVIRONMENTAL,2, true),
            new KitsManager.StartKits(Material.FISHING_ROD, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 8),
    };
    public static KitsManager.StartKits[] trollKit = {
            new KitsManager.StartKits(Material.IRON_SWORD, 1, Enchantment.DAMAGE_ALL, 1, true),
            new KitsManager.StartKits(Material.STICK, 1, Enchantment.KNOCKBACK, 3, true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, Enchantment.THORNS, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, Enchantment.THORNS, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, Enchantment.THORNS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, Enchantment.THORNS, 1, true),
            new KitsManager.StartKits(Material.SNOW_BALL, 64),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 4),
    };
    public static KitsManager.StartKits[] onepoundfishKit = {
            new KitsManager.StartKits(Material.RAW_FISH, 1, Enchantment.DAMAGE_ALL, 5, ChatColor.AQUA + "The One-Pound-Fish", true),
            new KitsManager.StartKits(Material.FISHING_ROD, 1, true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 4),
    };
    public static KitsManager.StartKits[] zombieKit = {
            new KitsManager.StartKits(Material.GOLD_SWORD, 1, Enchantment.DAMAGE_ALL, 2, ChatColor.GOLD + "Zombie Sword", true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 4),
    };
    public static KitsManager.StartKits[] dreadlordKit = {
            new KitsManager.StartKits(Material.IRON_SWORD, 1, Enchantment.DAMAGE_ALL, 1, ChatColor.DARK_GRAY + "Dreadlord Sword", true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 4),
    };
    public static KitsManager.StartKits[] bomberKit = {
            new KitsManager.StartKits(Material.IRON_SWORD, 1, Enchantment.DAMAGE_ALL, 1, ChatColor.DARK_GRAY + "Bomber's Sword", true),
            new KitsManager.StartKits(Material.BOW, 1, Enchantment.ARROW_DAMAGE, 1, ChatColor.RED + "Explosive Bow", true),
            new KitsManager.StartKits(Material.ARROW, 32),
            new KitsManager.StartKits(Material.TNT, 15),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 4),
    };
    public static KitsManager.StartKits[] meatKit = {
            new KitsManager.StartKits(Material.COOKED_BEEF, 1, Enchantment.DAMAGE_ALL, 6, true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 4),
    };
    public static KitsManager.StartKits[] snowmanKit = {
            new KitsManager.StartKits(Material.DIAMOND_SPADE, 1, Enchantment.DAMAGE_ALL, 4, true),
            new KitsManager.StartKits(Material.SNOW_BALL, 64),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 5),
    };
    public static KitsManager.StartKits[] spiderKit = {
            new KitsManager.StartKits(Material.IRON_SWORD, 1, Enchantment.DAMAGE_ALL, 1, ChatColor.GRAY + "Spider Sword", true),
            new KitsManager.StartKits(Material.WEB, 12),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, Enchantment.PROTECTION_FALL, 10, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 3),
    };
    public static KitsManager.StartKits[] speleoKit = {
            new KitsManager.StartKits(Material.DIAMOND_PICKAXE, 1, Enchantment.DAMAGE_ALL, 3, true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.DIAMOND_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.LEATHER_BOOTS, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 2, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 6),
    };
    public static KitsManager.StartKits[] ecologistKit = {
            new KitsManager.StartKits(Material.DIAMOND_AXE, 1, Enchantment.DAMAGE_ALL, 2, true),
            new KitsManager.StartKits(Material.FISHING_ROD, 1, true),
            new KitsManager.StartKits(Material.IRON_HELMET, 1, true),
            new KitsManager.StartKits(Material.IRON_CHESTPLATE, 1, true),
            new KitsManager.StartKits(Material.IRON_LEGGINGS, 1, true),
            new KitsManager.StartKits(Material.IRON_BOOTS, 1, true),
            new KitsManager.StartKits(Material.COOKED_BEEF, 64),
            new KitsManager.StartKits(Material.GOLDEN_APPLE, 6),
    };

}
