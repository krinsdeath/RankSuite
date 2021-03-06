package net.krinsoft.ranksuite.commands;

import net.krinsoft.ranksuite.Rank;
import net.krinsoft.ranksuite.RankCore;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Set;

/**
 * @author krinsdeath
 */
public class ValidateCommand extends BaseCommand {
    private final String rm;
    private final String add;

    public ValidateCommand(RankCore plugin) {
        super(plugin);
        setName("validate");
        addUsage(null, null, "Validates ranks for all known users to prevent group conflicts.");
        setPermission("ranksuite.validate");
        Plugin plg = this.plugin.getServer().getPluginManager().getPlugin("bPermissions");
        if (plg != null) {
            this.rm = "exec u:%1$s a:rmgroup v:%2$s";
            this.add = "exec u:%1$s a:addgroup v:%2$s";
            return;
        }
        plg = this.plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
        if (plg != null) {
            this.rm = "pex user %1$s group remove %2$s";
            this.add = "pex user %1$s group add %2$s";
            return;
        }
        plg = this.plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");
        if (plg != null) {
            this.rm = "permissions player removegroup %1$s %2$s";
            this.add = "permissions player addgroup %1$s %2$s";
            return;
        }
        this.rm = "";
        this.add = "";
    }

    public void runCommand(CommandSender sender, List<String> args) {
        if (!checkPermission(sender)) {
            noPermission(sender);
            return;
        }
        // this command can take a while to run if you have a large player list
        if (rm.length() > 0 && add.length() > 0) {
            long time = System.nanoTime();
            try {
                Set<Rank> ranks = this.plugin.getRanks();
                ConsoleCommandSender console = this.plugin.getServer().getConsoleSender();
                for (String p : this.plugin.getDB().getKeys(false)) {
                    resetUser(p);
                }
            } catch (NullPointerException e) {
                sender.sendMessage(ChatColor.RED + "Something went wrong while validating the user's ranks! Check server log for details.");
                e.printStackTrace();
                return;
            }
            this.plugin.saveDB();
            sender.sendMessage(String.format(ChatColor.GREEN + "[RankSuite] All users validated in %1$dms.", (System.nanoTime() - time) / 1000000));
            return;
        }
        sender.sendMessage(ChatColor.RED + "Validation failed. No compatible permissions plugin was found.");
    }

    public boolean resetUser(String user) {
        ConsoleCommandSender console = plugin.getServer().getConsoleSender();
        Set<Rank> ranks = this.plugin.getRanks();
        OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(user);
        if (player == null) { return false; }
        user = player.getName().toLowerCase();
        if (this.plugin.getDB().getInt(user) == 0) {
            this.plugin.getDB().set(user, null);
            this.plugin.debug("Removing '" + user + "' from rankings.");
            return true;
        }
        for (Rank rank : ranks) {
            this.plugin.getServer().dispatchCommand(console, String.format(rm, player.getName(), rank.getName()));
        }
        this.plugin.getServer().dispatchCommand(console, String.format(add, player.getName(), this.plugin.getRank(this.plugin.getDB().getInt(user)).getName()));
        return true;
    }

}
