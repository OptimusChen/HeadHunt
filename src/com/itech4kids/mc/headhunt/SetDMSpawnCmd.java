package com.itech4kids.mc.headhunt;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetDMSpawnCmd implements CommandExecutor {
    HeadHunt headhunt;

    public SetDMSpawnCmd(HeadHunt headhunt) {
        this.headhunt = headhunt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();

            if ( (headhunt.gameState != GameState.INIT) &&
                    (headhunt.gameState != GameState.READY) ) {
                player.sendMessage("The spawn location can't be changed once the game starts");
            }
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /SetDMSpawnCmd <1/2> to send the corners of the death arena");
            } else {
                int index = Integer.parseInt(args[0]);
                if ((index == 1) || (index == 2)) {
                    headhunt.setDMArenaSpawn(index, location);
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /SetDMSpawnCmd <1/2> to send the corners of the death arena");
                }
            }

        }
        return true;
    }
}
