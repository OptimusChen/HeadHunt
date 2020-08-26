package com.itech4kids.mc.headhunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.Console;

public class YellCmd implements CommandExecutor {
    HeadHunt headhunt;

    public YellCmd(HeadHunt headhunt) {
        this.headhunt = headhunt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // check whether the player is operator
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You must be an operator to run this command");
                return false;
            }

            // Check whether the spawn location is setup
            if (!headhunt.battleArena.isReady() || !headhunt.deathMatchArena.isReady()) {
                player.sendMessage("You need to setup the game first, use /setSpawn and /SetDMSpawn");
                return false;
            }

            // start timer to count
            int joinTime = headhunt.getJoinTime();
            headhunt.joinTimer = new CountdownTimer(headhunt,
                    joinTime,
                    () -> headhunt.displayJoinMessage(),
                    () -> headhunt.startGame(),
                    (t) -> headhunt.updateScoreBoard()
            );

            headhunt.joinTimer.scheduleTimer();
        }

        return true;
    }
}
