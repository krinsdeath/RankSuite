package net.krinsoft.ranksuite.commands;


import net.krinsoft.ranksuite.RankCore;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author krinsdeath
 */
public class CommandHandler {

    private Map<String, Command> commands = new HashMap<String, Command>();

    public CommandHandler(RankCore plugin) {
        commands.put("check", new CheckCommand(plugin));
        commands.put("add", new AddCommand(plugin));
        //commands.put("reset", new ResetCommand(plugin));
        commands.put("leaders", new LeaderCommand(plugin));
        commands.put("reload", new ReloadCommand(plugin));
        commands.put("validate", new ValidateCommand(plugin));
    }

    public void runCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0 || commands.get(args[0].toLowerCase()) == null) {
            sender.sendMessage(ChatColor.GREEN + "===" + ChatColor.GOLD + " RankSuite Help " + ChatColor.GREEN + "===");
            for (Command cmd : commands.values()) {
                if (cmd.checkPermission(sender)) {
                    cmd.showHelp(sender, label);
                }
            }
            return;
        }
        List<String> arguments = new ArrayList<String>(Arrays.asList(args));
        Command cmd = commands.get(arguments.remove(0).toLowerCase());
        if (arguments.size() < cmd.getRequiredArgs()) {
            cmd.showHelp(sender, label);
            return;
        }
        cmd.runCommand(sender, arguments);
    }

}
