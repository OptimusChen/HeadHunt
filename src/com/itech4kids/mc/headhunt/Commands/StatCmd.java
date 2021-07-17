package com.itech4kids.mc.headhunt.Commands;

import com.itech4kids.mc.headhunt.HeadHunt;
import com.itech4kids.mc.headhunt.Objects.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class StatCmd implements CommandExecutor {

    private HeadHunt headhunt;

    public StatCmd(HeadHunt headhunt) {
        this.headhunt = headhunt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        String name = target.getName();
        int i = Integer.parseInt(args[2]);
        try {
            switch (args[1].toLowerCase()){
                case "coins":
                    Config.addCoins(name, i);
                    target.sendMessage(ChatColor.GOLD + sender.getName() + " gave you " + i + " coin(s)!");
                    target.playSound(target.getLocation(), Sound.NOTE_PLING, 8, 8);
                    break;
                case "wins":
                    Config.addWins(name, i);
                    target.sendMessage(ChatColor.GREEN + sender.getName() + " gave you " + i + " win(s)!");
                    target.playSound(target.getLocation(), Sound.NOTE_PLING, 8, 8);
                    break;
                case "deaths":
                    Config.addDeath(name, i);
                    target.sendMessage(ChatColor.RED + sender.getName() + " gave you " + i + " death(s)!");
                    target.playSound(target.getLocation(), Sound.VILLAGER_NO, 8, 8);
                    break;
                case "xp":
                    Config.addXp(name, i, "Admin Commands");
                    target.sendMessage(ChatColor.GREEN + sender.getName() + " gave you " + i + " xp!");
                    target.playSound(target.getLocation(), Sound.NOTE_PLING, 8, 8);
                    break;
                case "kills":
                    Config.addKills(name, i);
                    target.sendMessage(ChatColor.GREEN + sender.getName() + " gave you " + i + " kill(s)!");
                    target.playSound(target.getLocation(), Sound.NOTE_PLING, 8, 8);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
