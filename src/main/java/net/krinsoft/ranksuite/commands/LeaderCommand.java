package net.krinsoft.ranksuite.commands;

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
        try {
            int page = (args.size() == 0 ? 0 : Integer.parseInt(args.get(0)));
            LinkedHashMap<String, Integer> map = plugin.getLeaders(page);
            sender.sendMessage(ChatColor.GREEN + "===" + ChatColor.GOLD + " Rank Leaders [Page " + (page+1) + "] " + ChatColor.GREEN + "===");
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                sender.sendMessage(ChatColor.AQUA + entry.getKey() + ChatColor.GREEN + ": " + ChatColor.GOLD + entry.getValue());
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "The page must be a " + ChatColor.GREEN + "positive number" + ChatColor.RED + ".");
        }
    }

}
