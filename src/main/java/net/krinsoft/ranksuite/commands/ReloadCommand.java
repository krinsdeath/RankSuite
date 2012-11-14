package net.krinsoft.ranksuite.commands;

import net.krinsoft.ranksuite.RankCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author krinsdeath
 */
public class ReloadCommand extends BaseCommand {

    public ReloadCommand(RankCore plugin) {
        super(plugin);
        setName("reload");
        addUsage(null, null, "Reloads RankSuite's configuration file.");
        setPermission("ranksuite.reload");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!checkPermission(sender)) {
            noPermission(sender);
            return;
        }
        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "RankSuite's configuration file has been reloaded.");
    }
}
