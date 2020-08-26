package com.itech4kids.mc.headhunt;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCmd implements CommandExecutor {
    HeadHunt headhunt;

    public SetSpawnCmd(HeadHunt headhunt) {
        this.headhunt = headhunt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();

            if ( (headhunt.gameState != GameState.INIT) &&
                    (headhunt.gameState != GameState.READY) )
            {
                player.sendMessage("The spawn location can't be changed once the game starts");
            } else if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /setSpawn <1/2> to send the corners of the arena");
            } else {
                int index = Integer.parseInt(args[0]);
                if ( (index == 1) || (index == 2) ) {
                    headhunt.setArenaSpawn(index, location);
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /setSpawn <1/2> to send the corners of the arena");
                }
            }
        }
        return true;
    }
}
