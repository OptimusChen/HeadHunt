package com.itech4kids.mc.headhunt.Objects;

import com.itech4kids.mc.headhunt.HeadHunt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {

    private static HeadHunt main;

    public Config(HeadHunt m){
        main = m;
        main.getConfig().options().copyDefaults();
        main.saveDefaultConfig();
    }

    public static boolean hasUnlockedKit(Player player, KitsType kit){
        boolean b = false;
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+player.getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ArrayList<String> kits = (ArrayList<String>) config.getStringList("unlocked-kits");
        if (kits.contains(kit.name())){
            b = true;
        }
        return b;
    }

    public static boolean hasUnlockedPerk(Player player, PerksType kit){
        boolean b = false;
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+player.getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ArrayList<String> kits = (ArrayList<String>) config.getStringList("unlocked-perks");
        if (kits.contains(kit.name())){
            b = true;
        }
        return b;
    }

    public static boolean hasUnlockedPanel(Player player, PanelType panel){
        boolean b = false;
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+player.getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ArrayList<String> panels = (ArrayList<String>) config.getStringList("unlocked-panel-items");
        if (panels.contains(panel.name())){
            b = true;
        }
        return b;
    }

    public static void addPerk(String name, PerksType kitsType) throws IOException {
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> kits = config.getStringList("unlocked-perks");
        kits.add(kitsType.name());
        config.set("unlocked-perks", kits);
        config.save(file);
    }

    public static void addKit(String name, KitsType kitsType) throws IOException {
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> kits = config.getStringList("unlocked-kits");
        kits.add(kitsType.name());
        config.set("unlocked-kits", kits);
        config.save(file);
    }

    public static void addPanel(String name, PanelType panelType) throws IOException {
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> panels = config.getStringList("unlocked-panel-items");
        panels.add(panelType.name());
        config.set("unlocked-panel-items", panels);
        config.save(file);
    }

    public static int getCoins(String name){
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getInt("coins");
    }

    public static int getDeaths(String name){
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getInt("deaths");
    }

    public static int getKills(String name){
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getInt("kills");
    }

    public static int getWins(String name){
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getInt("wins");
    }

    public static void addCoins(String name, int i) throws IOException {
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int a = config.getInt("coins");
        a = a + i;
        config.set("coins", a);
        config.save(file);
    }

    public static void addWins(String name, int i) throws IOException {
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int a = config.getInt("wins");
        a = a + i;
        config.set("wins", a);
        config.save(file);
    }

    public static void addKills(String name, int i) throws IOException {
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int a = config.getInt("kills");
        a = a + i;
        config.set("kills", a);
        config.save(file);
    }

    public static void addDeath(String name, int i) throws IOException {
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int a = config.getInt("deaths");
        a = a + i;
        config.set("deaths", a);
        config.save(file);
    }

    public static void addXp(String name, int i, String reason) throws IOException {
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int a = config.getInt("xp");
        //int j = config.getInt("star-level");
        a = a + i;
        config.set("xp", a);
        config.save(file);
        Bukkit.getPlayer(name).sendMessage(ChatColor.AQUA + "+" + i + " EXP! (" + reason + ")");
    }

    public static int getStarLevel(String name) {
        File file = new File(main.getDataFolder() + File.separator + "Players" + File.separator + Bukkit.getPlayer(name).getUniqueId() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int xp = config.getInt("xp");
        int level = config.getInt("star-level");
        level = Math.round(xp/100 + 1);

        /*
        if (a >= 0 && a < 10){
            j = 1;
        }else if (a > 20 && a < 30){
            j = 2;
        }else if (a > 30 && a < 50){
            j = 3;
        }else if (a > 50 && a < 70){
            j = 4;
        }else if (a > 70 && a < 100){
            j = 5;
        }else if (a > 100 && a < 130){
            j = 6;
        }else if (a > 130 && a < 170){
            j = 7;
        }else if (a > 170 && a < 210){
            j = 8;
        }else if (a > 210 && a < 260){
            j = 9;
        }else if (a > 260 && a < 310){
            j = 10;
        }else if (a > 310 && a < 370){
            j = 11;
        }else if (a > 370 && a < 430){
            j = 12;
        }else if (a > 430 && a < 510){
            j = 13;
        }else if (a > 510 && a < 590){
            j = 14;
        }else if (a > 590 && a < 680){
            j = 15;
        }else if (a > 680 && a < 770){
            j = 16;
        }else if (a > 770 && a < 870){
            j = 17;
        }else if (a > 870 && a < 970){
            j = 18;
        }else if (a > 970 && a < 1080){
            j = 19;
        }else{
            j = 20;
        }
         */
        return level;
    }

    public static int getUnlockedKitsInt(String name){
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getStringList("unlocked-kits").size();
    }

    public static int getUnlockedPanelsInt(String name){
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getStringList("unlocked-panel-items").size();
    }

    public static int getUnlockedPerksInt(String name){
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getStringList("unlocked-perks").size();
    }

    public static int getXp(String name){
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getInt("xp");
    }

    public static ChatColor getStarColor(String name){
        File file = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int j = config.getInt("star-level");
        ChatColor color = (ChatColor) config.get("star-color");
        if (j == 1){
            color = ChatColor.WHITE;
        }else if (j == 2){
            color = ChatColor.WHITE;
        }else if (j == 3){
            color = ChatColor.WHITE;
        }else if (j == 4){
            color = ChatColor.WHITE;
        }else if (j == 5){
            color = ChatColor.WHITE;
        }else if (j == 6){
            color = ChatColor.GREEN;
        }else if (j == 7){
            color = ChatColor.GREEN;
        }else if (j == 8){
            color = ChatColor.GREEN;
        }else if (j == 9){
            color = ChatColor.GREEN;
        }else if (j == 10){
            color = ChatColor.GREEN;
        }else if (j == 11){
            color = ChatColor.AQUA;
        }else if (j == 12){
            color = ChatColor.AQUA;
        }else if (j == 13){
            color = ChatColor.AQUA;
        }else if (j == 14){
            color = ChatColor.AQUA;
        }else if (j == 15){
            color = ChatColor.AQUA;
        }else if (j == 16){
            color = ChatColor.GOLD;
        }else if (j == 17){
            color = ChatColor.GOLD;
        }else if (j == 18){
            color = ChatColor.GOLD;
        }else if (j == 19){
            color = ChatColor.GOLD;
        }else if (j == 20){
            color = ChatColor.BLACK;
        }
        return color;
    }

    public static void createPlayer(String name) throws IOException {
        File folder = new File(main.getDataFolder() + File.separator + "Players");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File playerFile = new File(main.getDataFolder()+File.separator+"Players"+File.separator+Bukkit.getPlayer(name).getUniqueId()+".yml");
        if (!playerFile.exists()) {
            playerFile.createNewFile();
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            ArrayList<String> kits = new ArrayList<>();
            ArrayList<String> perks = new ArrayList<>();
            ArrayList<String> panel = new ArrayList<>();
            ChatColor color = ChatColor.WHITE;
            config.set("coins", 0);
            config.set("kills", 0);
            config.set("wins", 0);
            config.set("deaths", 0);
            config.set("star-level", 1);
            config.set("star-color", color);
            config.set("xp", 0);
            config.set("unlocked-perks", perks);
            config.set("unlocked-kits", kits);
            config.set("unlocked-panel-items", panel);
            config.save(playerFile);
        }
    }



}
