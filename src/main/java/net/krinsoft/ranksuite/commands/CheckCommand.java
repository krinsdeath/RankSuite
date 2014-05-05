package net.krinsoft.ranksuite.commands;

import java.util.List;

import net.krinsoft.ranksuite.RankCore;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author krinsdeath
 */
public class CheckCommand extends BaseCommand {

	public CheckCommand(RankCore plugin) {
		super(plugin);
		setName("check");
		addUsage(null, null, "Check your own rank");
		addUsage("[user]", null, "Check another user's rank.");
		setPermission("ranksuite.check");
	}

	public void runCommand(CommandSender sender, List<String> args) {
		if (!checkPermission(sender)) {
			noPermission(sender);
			return;
		}
		OfflinePlayer target;
		if (args.size() == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must supply a "
						+ ChatColor.GREEN + "player target" + ChatColor.RED
						+ " to use this command from the console.");
				return;
			}
			target = (Player) sender;
		} else {
			if (!(sender instanceof Player)
					&& !sender.hasPermission("ranksuite.check.other")) {
				sender.sendMessage(ChatColor.RED
						+ "You do not have permission to use that command!");
				return;
			}
			target = plugin.getServer().getOfflinePlayer(args.get(0));
			if (target == null) {
				sender.sendMessage(ChatColor.RED
						+ "No player found with the name '" + ChatColor.GREEN
						+ args.get(0) + ChatColor.RED + "'!");
				return;
			}
		}
		plugin.checkRank(sender, target);
	}

}
