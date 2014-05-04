package net.krinsoft.ranksuite.commands;

import net.krinsoft.ranksuite.RankCore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author krinsdeath
 */
public class FixCommand extends BaseCommand {

    public FixCommand(RankCore plugin) {
        super(plugin);
        setName("fix");
        addUsage("[user]", null, "Validate the specified user.");
        setRequiredArgs(1);
        setPermission("ranksuite.fix");
    }

    public void runCommand(CommandSender sender, List<String> args) {
        ValidateCommand command = (ValidateCommand) plugin.getCommandHandler().getCommand("validate");
        OfflinePlayer op = Bukkit.getOfflinePlayer(args.get(0));
        boolean reset = command.resetUser(op.getUniqueId());
        if (reset) {
            sender.sendMessage(ChatColor.GREEN + "The player '" + ChatColor.AQUA + args.get(0) + ChatColor.GREEN + "' was successfully fixed.");
        } else {
            sender.sendMessage(ChatColor.RED + "No player found with the name '" + ChatColor.GREEN + args.get(0) + ChatColor.RED + "'!");
        }
    }
}
