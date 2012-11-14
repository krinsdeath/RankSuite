package net.krinsoft.ranksuite.commands;

import net.krinsoft.ranksuite.Leader;
import net.krinsoft.ranksuite.RankCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author krinsdeath
 */
public class LeaderCommand extends BaseCommand {

    public LeaderCommand(RankCore plugin) {
        super(plugin);
        setName("leaders");
        addUsage("[page]", null, "Shows the current playtime leaders.");
        setPermission("ranksuite.leaders");
    }

    public void runCommand(CommandSender sender, List<String> args) {
        if (!checkPermission(sender)) {
            noPermission(sender);
            return;
        }
        try {
            int page = (args.size() == 0 ? 1 : Integer.parseInt(args.get(0)));
            LinkedHashMap<Integer, Leader> map = plugin.getLeaders(page - 1);
            sender.sendMessage(ChatColor.GREEN + "===" + ChatColor.GOLD + " Rank Leaders " + ChatColor.GREEN + "===");
            for (Map.Entry<Integer, Leader> entry : map.entrySet()) {
                sender.sendMessage(String.format("%1$-" + 4 + "s", (entry.getKey() + 1)) +
                        ChatColor.GREEN + " | " + ChatColor.GOLD + String.format("%1$-" + 17 + "s", entry.getValue().getName()) +
                        ChatColor.GREEN + " | " + ChatColor.AQUA + plugin.getRank(entry.getValue().getTimePlayed()).getName());
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "The page must be a " + ChatColor.GREEN + "positive number" + ChatColor.RED + ".");
        }
    }

}
