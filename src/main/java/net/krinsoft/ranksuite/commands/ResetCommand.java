package net.krinsoft.ranksuite.commands;

import net.krinsoft.ranksuite.RankCore;
import net.krinsoft.ranksuite.RankedPlayer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author krinsdeath
 */
public class ResetCommand extends BaseCommand {

    public ResetCommand(RankCore plugin) {
        super(plugin);
        setName("reset");
        addUsage("[user]", null, "Resets the playtime for the specifed user to 0.");
        setRequiredArgs(1);
        setPermission("ranksuite.reset");
    }

    public void runCommand(CommandSender sender, List<String> args) {
        if (!checkPermission(sender)) {
            noPermission(sender);
            return;
        }
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args.get(0));
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "No player found with the name '" + ChatColor.GREEN + args.get(0) + ChatColor.RED + "'!");
            return;
        }
        RankedPlayer player = plugin.getPlayer(target.getName());
        int time = player.getTimePlayed();
        player.reset();
        sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s playtime has been reset!");
        sender.sendMessage(ChatColor.RED + "Previous playtime: " + ChatColor.AQUA + time);
    }
}
