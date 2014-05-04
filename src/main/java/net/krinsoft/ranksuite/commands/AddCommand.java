package net.krinsoft.ranksuite.commands;

import net.krinsoft.ranksuite.RankCore;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author krinsdeath
 */
public class AddCommand extends BaseCommand {

    public AddCommand(RankCore plugin) {
        super(plugin);
        setName("add");
        addUsage("[user]", "[mins]", "Adds playtime to the specified user.");
        setRequiredArgs(2);
        setPermission("ranksuite.add");
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
            sender.sendMessage(ChatColor.RED + "Time added must be a " + ChatColor.GREEN + "positive number" + ChatColor.RED + ".");
            return;
        }
        if (mins <= 0) {
            sender.sendMessage(ChatColor.RED + "Time added must be a " + ChatColor.GREEN + "positive number" + ChatColor.RED + ".");
            return;
        }
        plugin.getPlayer(target.getUniqueId()).addTime(mins);
        sender.sendMessage(ChatColor.AQUA + String.valueOf(mins) + ChatColor.GREEN + " minute" + (mins > 1 ? "s have" : " has") + " been added to " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + ".");
    }

}
