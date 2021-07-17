package com.itech4kids.mc.headhunt.Commands;

import com.itech4kids.mc.headhunt.HeadHunt;
import com.itech4kids.mc.headhunt.Objects.CountdownTimer;
import com.itech4kids.mc.headhunt.Objects.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class YellCmd implements CommandExecutor {
    HeadHunt headhunt;

    public YellCmd(HeadHunt headhunt) {
        this.headhunt = headhunt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // check whether the player is operator
        Player player = (Player) sender;
        if (headhunt.gameState.equals(GameState.INIT)){
            if (sender instanceof Player) {

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
                        () -> {
                            try {
                                headhunt.startGame();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        },
                        (t) -> {
                            try {
                                headhunt.updateScoreBoard();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                );

                headhunt.joinTimer.scheduleTimer();
            }
        }else if (headhunt.gameState.equals(GameState.BATTLE)){
            player.sendMessage(ChatColor.RED + "The game has already started");
        }else{
            player.sendMessage(ChatColor.RED + "/yell has already been executed! Use /join to join the game!");
        }

        return true;
    }
}
