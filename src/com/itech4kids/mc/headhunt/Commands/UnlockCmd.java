package com.itech4kids.mc.headhunt.Commands;

import com.itech4kids.mc.headhunt.HeadHunt;
import com.itech4kids.mc.headhunt.Objects.KitsType;
import com.itech4kids.mc.headhunt.Objects.Config;
import com.itech4kids.mc.headhunt.Objects.PerksType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class UnlockCmd implements CommandExecutor {

    private HeadHunt headhunt;

    public UnlockCmd(HeadHunt headhunt) {
        this.headhunt = headhunt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        String name = player.getName();
        if (player.isOp()) {
            if (args.length == 0){
                player.sendMessage(ChatColor.RED + "Kits or Perks?");
            }else if (args[0].equalsIgnoreCase("Perks")){
                player.sendMessage(ChatColor.GREEN + "Unlocked all perks!");
                try {
                    Config.addPerk(name, PerksType.ARROW_RECOVERY);
                    Config.addPerk(name, PerksType.BARBARIAN);
                    Config.addPerk(name, PerksType.BLACK_MAGIC);
                    Config.addPerk(name, PerksType.BLAZING_ARROWS);
                    Config.addPerk(name, PerksType.BULLDOZER);
                    Config.addPerk(name, PerksType.JUGGERNAUT);
                    Config.addPerk(name, PerksType.KNOWLEDGE);
                    Config.addPerk(name, PerksType.LUCKY_CHARM);
                    Config.addPerk(name, PerksType.MARKSMANSHIP);
                    Config.addPerk(name, PerksType.RESISTANCE_BOOST);
                    Config.addPerk(name, PerksType.SAVIOR);
                    Config.addPerk(name, PerksType.TELEKINESIS);
                    Config.addPerk(name, PerksType.FIRE_ASPECT);
                    Config.addPerk(name, PerksType.ARROW_SILVERFISH);
                    Config.addPerk(name, PerksType.GAUNTLET);
                    Config.addPerk(name, PerksType.WEAKNESS);
                    Config.addPerk(name, PerksType.DIAMOND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (args[0].equalsIgnoreCase("Kits")){
                player.sendMessage(ChatColor.GREEN + "Unlocked all kits!");
                try {
                    Config.addKit(name, KitsType.ARCHER);
                    Config.addKit(name, KitsType.BERSERKER);
                    Config.addKit(name, KitsType.BOMBER);
                    Config.addKit(name, KitsType.DREADLORD);
                    Config.addKit(name, KitsType.ENDERMAN);
                    Config.addKit(name, KitsType.FARMER);
                    Config.addKit(name, KitsType.FISHERMAN);
                    Config.addKit(name, KitsType.HEROBRINE);
                    Config.addKit(name, KitsType.MEATMASTER);
                    Config.addKit(name, KitsType.ONEPOUNDFISH);
                    Config.addKit(name, KitsType.PIGMAN);
                    Config.addKit(name, KitsType.PIGRIDER);
                    Config.addKit(name, KitsType.PYROMANCER);
                    Config.addKit(name, KitsType.SCOUT);
                    Config.addKit(name, KitsType.SLOTH);
                    Config.addKit(name, KitsType.SNOWMAN);
                    Config.addKit(name, KitsType.TANK);
                    Config.addKit(name, KitsType.TROLL);
                    Config.addKit(name, KitsType.ZOMBIE);
                    Config.addKit(name, KitsType.SPELEOLOGIST);
                    Config.addKit(name, KitsType.SPIDER);
                    Config.addKit(name, KitsType.ECOLOGIST);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
