package net.krinsoft.ranksuite.commands;

import net.krinsoft.ranksuite.Leader;
import net.krinsoft.ranksuite.RankCore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author milkywayz
 */
public class ConvertCommand extends BaseCommand {

	public ConvertCommand(RankCore plugin) {
		super(plugin);
		setName("convert");
		addUsage("[player]", null,
				"Converts player to UUID form, if empty converts ALL players");
		setPermission("ranksuite.convert");
	}

	@SuppressWarnings("deprecation")
	public void runCommand(CommandSender sender, List<String> args) {
		if (!checkPermission(sender)) {
			noPermission(sender);
			return;
		}
		if (args.size() == 0) {
			//TODO: Divide and Conquer!!!
			// This will lag out the server, lets divide and conquer to fix it
			Bukkit.getScheduler().runTask(plugin, new Runnable() {

				public void run() {
					for (String key : plugin.getPlayersDB().getKeys(false)) {
						UUID uuid = Bukkit.getOfflinePlayer(key).getUniqueId();
						if (plugin.getPlayersDB().getInt(name) == -1) {
							continue;
						}
						int time = plugin.getPlayersDB().getInt(name.toLowerCase());
						plugin.getUuidDB().set(uuid.toString(), time);
						plugin.getPlayersDB().set(name.toLowerCase(), -1);

					}
				}

			});
			plugin.savePlayersDB();
			plugin.saveUuidDB();
			plugin.buildLeaderboard();
			sender.sendMessage(ChatColor.GREEN
					+ "Finished converting ALL players to UUID format");
		} else {
			final String name = args.get(0);
			UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
			plugin.transfer(name, uuid);
			sender.sendMessage(ChatColor.GREEN + "Converted " + name
					+ " to UUID format in the DB");
		}
	}
}
