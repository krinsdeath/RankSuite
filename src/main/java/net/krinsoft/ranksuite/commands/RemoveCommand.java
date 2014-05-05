package net.krinsoft.ranksuite.commands;

import net.krinsoft.ranksuite.RankCore;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author krinsdeath
 */
public class RemoveCommand extends BaseCommand {

    public RemoveCommand(RankCore plugin) {
        super(plugin);
        setName("remove");
        addUsage("[user]", "[mins]", "Removes playtime from the specified user.");
        setRequiredArgs(2);
        setPermission("ranksuite.remove");
    }

    public void runCommand(CommandSender sender, List<String> args) {
        if (!checkPermission(sender)) {
            noPermission(sender);
            return;
        }
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args.get(0));
        int mins;
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "No player found with the name '" + ChatColor.GREEN + args.get(0) + ChatColor.RED + "'!");
            return;
        }
        try {
            mins = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Time removed must be a " + ChatColor.GREEN + "positive number" + ChatColor.RED + ".");
            return;
        }
        if (mins <= 0) {
            sender.sendMessage(ChatColor.RED + "Time removed must be a " + ChatColor.GREEN + "positive number" + ChatColor.RED + ".");
            return;
        }
        plugin.getPlayer(target.getUniqueId()).removeTime(mins);
        sender.sendMessage(ChatColor.AQUA + String.valueOf(mins) + ChatColor.GREEN + " minute" + (mins > 1 ? "s have" : " has") + " been removed from " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + ".");    }
}
