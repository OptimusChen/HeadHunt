package com.itech4kids.mc.headhunt.Commands;

import com.itech4kids.mc.headhunt.HeadHunt;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCmd implements CommandExecutor {
    HeadHunt main;

    public SpawnCmd(HeadHunt headhunt) {
        this.main = headhunt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (player.isOp()){
                main.getConfig().set("lobby-spawn", player.getLocation());
            }
        }
        return false;
    }
}
