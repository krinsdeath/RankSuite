package net.krinsoft.ranksuite.commands;

import net.krinsoft.ranksuite.RankCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author krinsdeath
 */
public abstract class BaseCommand implements Command {
    protected RankCore plugin;
    protected String name;
    protected String permission;
    protected int required = 0;
    protected List<String> usages = new ArrayList<String>();

    public BaseCommand(RankCore plugin) {
        this.plugin = plugin;
    }

    protected final void setName(String name) {
        this.name = name;
    }

    protected final void setPermission(String perm) {
        this.permission = perm;
    }

    protected final void setRequiredArgs(int req) {
        this.required = req;
    }

    protected final void addUsage(String sub1, String sub2, String description) {
        StringBuilder usage = new StringBuilder().append(ChatColor.BLUE).append(String.format("%1$-" + 8 + "s", this.name));
        if (sub1 != null) {
            usage.append(ChatColor.YELLOW);
            usage.append(String.format("%1$-" + 8 + "s", sub1));
        } else {
            usage.append(String.format("%1$-" + 8 + "s", ""));
        }
        if (sub2 != null) {
            usage.append(ChatColor.AQUA);
            usage.append(String.format("%1$-" + 8 + "s", sub2));
        } else {
            usage.append(String.format("%1$-" + 8 + "s", ""));
        }
        usage.append(ChatColor.GREEN);
        usage.append(description);
        this.usages.add(usage.toString());
    }

    public boolean checkPermission(CommandSender sender) {
        return sender.hasPermission(this.permission);
    }

    public void showHelp(CommandSender sender, String label) {
        for (String usage : usages) {
            sender.sendMessage(ChatColor.GRAY + String.format("%1$-" + 10 + "s", label) + usage);
        }
    }

    public int getRequiredArgs() {
        return this.required;
    }

    protected void noPermission(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
    }
}
