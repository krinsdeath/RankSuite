package net.krinsoft.ranksuite;

import net.krinsoft.ranksuite.commands.CommandHandler;
import net.krinsoft.ranksuite.events.RankSuiteRankChangeEvent;
import net.krinsoft.ranksuite.events.RankSuiteRankResetEvent;
import net.krinsoft.ranksuite.util.FancyParser;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author krinsdeath
 */
public class RankCore extends JavaPlugin {

    private final static Pattern LAST = Pattern.compile("\\[old\\]");
    private final static Pattern NEXT = Pattern.compile("\\[new\\]");
    private final static Pattern USER = Pattern.compile("\\[user\\]");

    @Deprecated
    private FileConfiguration players_db;
    private FileConfiguration uuids_db;

    private LinkedHashMap<String, Integer> leaders = new LinkedHashMap<String, Integer>();
    private LinkedList<UUID> logins = new LinkedList<UUID>();

    private int leaderTask;
    private int updateTask;
    private int loginTask;

    private CommandHandler commands;

    private Map<String, Rank> ranks = new HashMap<String, Rank>();
    private Map<UUID, RankedPlayer> players = new HashMap<UUID, RankedPlayer>();

    private boolean debug = false;
    private boolean log_ranks = true;
    private final List<String> promotion = new ArrayList<String>();

    public void onEnable() {
        // register the player join event
        RankListener listener = new RankListener(this);
        getServer().getPluginManager().registerEvents(listener, this);

        // fetch the config file and default values
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        this.debug = getConfig().getBoolean("plugin.debug", false);
        this.log_ranks = getConfig().getBoolean("plugin.log_rankups", true);
        this.promotion.clear();
        this.promotion.addAll(getConfig().getStringList("promote"));

        // build the ranks
        for (String rank : getConfig().getConfigurationSection("ranks").getKeys(false)) {
            ConfigurationSection section = getConfig().getConfigurationSection("ranks." + rank);
            Rank rank1 = new Rank(rank, section.getString("next_rank"), section.getInt("time_played"), section.getString("message"));
            ranks.put(rank, rank1);
        }

        // build a new rank database or import AutoRank's
        File file = new File("plugins/AutoRank/data.yml");
        if (file.exists()) {
            getLogger().info("Using AutoRank's data.yml for current rankings...");
            if (file.renameTo(new File(getDataFolder(), "players.db"))) {
                getLogger().info("Rankings imported.");
            }
        }
        getUuidDB();

        // create the leaderboard
        this.leaderTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                buildLeaderboard();
            }
        }, 1L, 36000L);

        // build the command handler
        commands = new CommandHandler(this);

        // register a scheduled task to update players dynamically
        this.updateTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for (UUID uuid : players.keySet()) {
                    promote(uuid);
                }
                saveUuidDB();
            }
        }, 1L, 6000L);

        // login task to handle players quickly without slowing down the server
        this.loginTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                if (logins.size() > 0) {
                    for (UUID uuid : logins) {
                        promote(uuid);
                    }
                    logins.clear();
                }
            }
        }, 1L, 1L);
    }

    public void onDisable() {
    	saveUuidDB();
        getServer().getScheduler().cancelTasks(this);
        getServer().getScheduler().cancelTask(this.leaderTask);
        getServer().getScheduler().cancelTask(this.updateTask);
        getServer().getScheduler().cancelTask(this.loginTask);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        commands.runCommand(sender, label, args);
        return true;
    }

    @Deprecated
    public FileConfiguration getPlayersDB() {
        if (players_db == null) {
            players_db = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "players.db"));
        }
        return players_db;
    }

    @Deprecated
    public void savePlayersDB() {
        try {
            players_db.save(new File(getDataFolder(), "players.db"));
        } catch (IOException e) {
            getLogger().warning("An error occurred while saving the players database.");
        }
    }
    
    public FileConfiguration getUuidDB() {
    	 if (uuids_db == null) {
    		 uuids_db = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "uuids.db"));
         }
         return uuids_db;
    }
    
    public void saveUuidDB() {
        try {
        	uuids_db.save(new File(getDataFolder(), "uuids.db"));
        } catch (IOException e) {
            getLogger().warning("An error occurred while saving the uuids database.");
        }
    }

    public CommandHandler getCommandHandler() {
        return this.commands;
    }

    /**
     * Attempts to reload the plugin's configuration file and restart the plugin tasks
     */
    public void reload() {
        getServer().getScheduler().cancelTasks(this);
        getServer().getScheduler().cancelTask(this.leaderTask);
        getServer().getScheduler().cancelTask(this.updateTask);
        getServer().getScheduler().cancelTask(this.loginTask);

        reloadConfig();
        getConfig().options().header(
                "You should always have one group whose time required is 0 minutes.\n" +
                "Otherwise RankSuite won't know what rank to put you in when you first start.");
        this.debug = getConfig().getBoolean("plugin.debug", false);
        this.log_ranks = getConfig().getBoolean("plugin.log_rankups", true);
        this.promotion.clear();
        this.promotion.addAll(getConfig().getStringList("promote"));

        ranks.clear();
        // build the ranks
        for (String rank : getConfig().getConfigurationSection("ranks").getKeys(false)) {
            ConfigurationSection section = getConfig().getConfigurationSection("ranks." + rank);
            Rank rank1 = new Rank(rank, section.getString("next_rank"), section.getInt("time_played"), section.getString("message"));
            ranks.put(rank, rank1);
        }

        // re-create the leaderboard
        this.leaderTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                buildLeaderboard();
            }
        }, 1L, 36000L);

        // re-register a scheduled task to update players dynamically
        this.updateTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for (UUID uuid : players.keySet()) {
                    promote(uuid);
                }
                saveUuidDB();
            }
        }, 1L, 6000L);

        // login task to handle players quickly without slowing down the server
        this.loginTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                if (logins.size() > 0) {
                    for (UUID uuid : logins) {
                        promote(uuid);
                    }
                    logins.clear();
                }
            }
        }, 1L, 1L);
    }

    /**
     * Writes out a rank up message
     * @param message The message being written
     */
    public void rank(String message) {
        if (log_ranks) {
            message = "[Rank Up!] " + message;
            getLogger().info(message);
        }
    }

    /**
     * Writes out a debug message
     * @param message The message being written
     */
    public void debug(String message) {
        if (debug) {
            String msg = "[Debug] " + message;
            getLogger().info(msg);
        }
    }

    /**
     * Populates the Leaderboard hashmap to maintain efficiency
     */
    public void buildLeaderboard() {
        debug("Initializing leaderboards...");
        Map<String, Integer> leaders = new LinkedHashMap<String, Integer>();
        //Switch to UUIDs
        for (String key : getUuidDB().getKeys(false)) {
        	UUID uuid = UUID.fromString(key);
        	OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            leaders.put(op.getName(), getUuidDB().getInt(key));
        }
        LinkedList<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(leaders.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        this.leaders.clear();
        for (Map.Entry<String, Integer> entry : list) {
            this.leaders.put(entry.getKey(), entry.getValue());
        }
        debug("Leaderboards built.");
    }

    /**
     * Inefficient and horrifying, don't use this ever.
     * @param page The page to begin searching on
     * @return A map of the leaders matching the specified page
     */
    public LinkedHashMap<Integer, Leader> getLeaders(int page) {
        int search = page * 10 + 10;
        if (leaders.size() / 10 < page) {
            page = 0;
        }
        if (search > leaders.size()) {
            search = leaders.size();
        }
        LinkedHashMap<Integer, Leader> map = new LinkedHashMap<Integer, Leader>();
        String[] array = leaders.keySet().toArray(new String[leaders.size()]);

        for (int i = page * 10; i < search; i++) {
            map.put(i, new Leader(array[i], leaders.get(array[i])));
        }
        return map;
    }

    /**
     * Fetches the specified player's position in the leaderboards
     * @param name The name of the player
     * @return The player's position in the leaderboards
     */
    public int getLeader(String name) {
        String[] array = leaders.keySet().toArray(new String[leaders.size()]);
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * One time method to move data from players.db to uuids.db
     * @param name The name of the player
     */
    public void transfer(String name, UUID uuid) {
    	//Check if we already converted
    	if(this.getPlayersDB().getInt(name) == -1) {
    		return;
    	}
    	if(this.getUuidDB().getInt(uuid.toString()) == 0) {
    		int time = this.getPlayersDB().getInt(name.toLowerCase());
    		debug("Converted [" + name + "] to [" + uuid.toString() + "] in storage, with [" + time + "] minutes");
        	this.getUuidDB().set(uuid.toString(), time);
        	this.getPlayersDB().set(name.toLowerCase(), -1);
        	this.savePlayersDB();
        	this.saveUuidDB();
        	this.buildLeaderboard();
    	}
    }

    /**
     * Fetches a RankedPlayer object for the specified player by their name
     * @param name The UUID of the player we're fetching
     * @return A RankedPlayer object representing the specified player
     */
    public RankedPlayer getPlayer(UUID uuid) {
        RankedPlayer player = players.get(uuid);
        if (player == null) {
            player = promote(uuid);
        }
        return player;
    }

    /**
     * Gets the specified rank by its name
     * @param name The name of the rank we're fetching
     * @return The rank object
     */
    public Rank getRank(String name) {
        return ranks.get(name);
    }

    /**
     * Fetches the highest possible rank according to the specified minutes
     * @param mins The number of minutes
     * @return The rank that was fetched.
     */
    public Rank getRank(int mins) {
        if (mins < 0) { mins = 0; }
        int highest = 0;
        int lowest = Integer.MAX_VALUE;
        Rank bottom = null;
        Rank top = null;
        for (Rank r : ranks.values()) {
            if (r.getMinutesRequired() <= mins && r.getMinutesRequired() >= highest) {
                highest = r.getMinutesRequired();
                top = r;
            }
            if (r.getMinutesRequired() <= lowest) {
                lowest = r.getMinutesRequired();
                bottom = r;
            }
        }
        return top == null ? bottom : top;
    }

    /**
     * Gets a set containing all currently registered ranks
     * @return The set of ranks
     */
    public Set<Rank> getRanks() {
        return new HashSet<Rank>(ranks.values());
    }

    /**
     * Adds the specified player name to the login queue
     * @param name The name of the player who has just logged in
     */
    public void login(final UUID uuid) {
        logins.add(uuid);
    }

    /**
     * Checks whether the specified player is qualified for a promotion, and promotes them if they are
     * @param uuid The UUID of the player
     */
    public RankedPlayer promote(final UUID uuid) {
        final OfflinePlayer promoted = getServer().getOfflinePlayer(uuid);       
        if (promoted == null) {
            getLogger().warning("Something went wrong... a fetched player was null!");
            return null;
        }
        final String name = promoted.getName();
        RankedPlayer player = players.get(uuid);
        if (player == null) {
        	//Switch to new DB, using UUIDs
            int minutes = getUuidDB().getInt(uuid.toString(), 0);
            Rank rank = getRank(minutes);
            if (rank == null) {
                getLogger().info("No matching rank was found! Check config.yml for invalid syntax.");
                rank = ranks.values().toArray(new Rank[ranks.size()])[0];
            }
            debug(name + " determined to be " + rank.getName() + " with " + minutes + " minute(s) played.");
            boolean exempt = !promoted.isOnline() || promoted.getPlayer().hasPermission("ranksuite.exempt");
            player = new RankedPlayer(this, name, uuid, rank, minutes, System.currentTimeMillis(), exempt);
        }
        if (player.addTime()) {
            // player is qualified for a promotion
            Rank last = player.getRank();
            Rank next = getRank(player.getRank().getNextRank());
            rank(promoted.getName() + " has advanced from " + last.getName() + " to " + next.getName() + "!");
            for (String cmd : this.promotion) {
                cmd = LAST.matcher(cmd).replaceAll(last.getName());
                cmd = NEXT.matcher(cmd).replaceAll(next.getName());
                cmd = USER.matcher(cmd).replaceAll(name);
                getServer().dispatchCommand(getServer().getConsoleSender(), cmd);
            }
            if (next.getPromotionMessage() != null) {
                Player p = promoted.getPlayer();
                if (p != null) {
                    for (String msg : next.getPromotionMessage().split("\n")) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    }
                }
            }
            RankSuiteRankChangeEvent event = new RankSuiteRankChangeEvent(name, player.getRank().getName(), next.getName());
            getServer().getPluginManager().callEvent(event);
            player.setRank(next);
        }
        if (promoted.isOnline()) {
            players.put(uuid, player);
        }
        return player;
    }

    /**
     * Checks for a promotion for the specified player and then destroys their currently loaded object
     * @param uuid The UUID of the player who is retiring
     */
    public void retire(UUID uuid) {
        promote(uuid);
        RankedPlayer p = players.remove(uuid);
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        debug(op.getName() + " has retired as a '" + p.getRank().getName() + "'");
    }

    /**
     * Tests whether the sender is allowed to check the player's rank, and then displays information about the specified player.
     * @param sender The sender who issued the command
     * @param player The player whose rank is being checked
     */
    public void checkRank(CommandSender sender, OfflinePlayer player) {
        if (sender.equals(player) || sender.hasPermission("ranksuite.check.other") || !(sender instanceof Player)) {
        	UUID uuid = player.getUniqueId();
            RankedPlayer p = promote(uuid);
            if (p == null) {
                sender.sendMessage(ChatColor.RED + "Something went wrong.");
                return;
            }
            if (p.addTime()) {
                // user qualified for a promotion
                promote(uuid);
            }
            boolean equal = sender.equals(player);
            String name = (equal ? "You" : player.getName());
            StringBuilder message = new StringBuilder();
            message.append(ChatColor.AQUA).append(name).append(ChatColor.GREEN).append(equal ? " are" : " is").append(" in the rank: ").append(ChatColor.AQUA).append(p.getRank().getName()).append("\n");
            message.append(ChatColor.AQUA).append(name).append(ChatColor.GREEN).append(equal ? " have" : " has").append(" played for ").append(ChatColor.AQUA).append(FancyParser.toFancyTime(p.getTimePlayed())).append(ChatColor.GREEN).append(".\n");
            message.append(ChatColor.AQUA).append(name).append(ChatColor.GREEN).append(equal ? " are" : " is").append(" ranked ").append(ChatColor.AQUA).append(FancyParser.toFancyOrdinal(getLeader(player.getName()) + 1)).append(ChatColor.GREEN).append(".\n");
            if (p.getRank().getNextRank() != null) {
                Rank next = getRank(p.getRank().getNextRank());
                if (next != null) {
                    message.append(ChatColor.AQUA).append(name).append(ChatColor.GREEN).append(" will rank up to ").append(ChatColor.AQUA).append(next.getName()).append(ChatColor.GREEN).append(" in ").append(ChatColor.AQUA).append(FancyParser.toFancyTime(next.getMinutesRequired() - p.getTimePlayed())).append(ChatColor.GREEN).append(".\n");
                }
            }
            for (String line : message.toString().split("\n")) {
                sender.sendMessage(line);
            }
        }
        if (!player.isOnline()) {
            players.remove(player.getName());
        }
    }

    /**
     * Resets the specified player to the default rank.
     * @param uuid The UUID of the player being reset.
     */
    public void reset(UUID uuid) {
        RankedPlayer p = getPlayer(uuid);
        reset(uuid, p.getRank().getName());
    }

    /**
     * Uses the specified player name and rank name to reset a player to the default rank
     * @param uuid The UUID of the player
     * @param rank The rank being removed from the player
     */
    public void reset(UUID uuid, String rank) {
        Rank base = getRank(0);
        reset(uuid, rank, base.getName());
    }

    /**
     * Resets the specified player to the specified base rank and removes the 'rank' from the player.
     * @param uuid The UUID of the player
     * @param rank The rank being removed from the player
     * @param base The player's new rank
     */
    public void reset(UUID uuid, String rank, String base) {
    	final String name = Bukkit.getOfflinePlayer(uuid).getName();
        RankSuiteRankResetEvent event = new RankSuiteRankResetEvent(name, base);
        getServer().getPluginManager().callEvent(event);
        Plugin plg = this.getServer().getPluginManager().getPlugin("bPermissions");
        if (plg != null) {
            getServer().dispatchCommand(getServer().getConsoleSender(), String.format("exec u:%s a:rmgroup v:%s", name, rank));
            getServer().dispatchCommand(getServer().getConsoleSender(), String.format("exec u:%s a:addgroup v:%s", name, base));
            return;
        }
        plg = this.getServer().getPluginManager().getPlugin("PermissionsEx");
        if (plg != null) {
            getServer().dispatchCommand(getServer().getConsoleSender(), String.format("pex user %s removegroup %s", name, rank));
            getServer().dispatchCommand(getServer().getConsoleSender(), String.format("pex user %s addgroup %s", name, base));
            return;
        }
        plg = this.getServer().getPluginManager().getPlugin("PermissionsBukkit");
        if (plg != null) {
            getServer().dispatchCommand(getServer().getConsoleSender(), String.format("permissions player removegroup %s %s", name, rank));
            getServer().dispatchCommand(getServer().getConsoleSender(), String.format("permissions player addgroup %s %s", name, base));
            return;
        }
        plg = this.getServer().getPluginManager().getPlugin("Privileges");
        if (plg != null) {
            getServer().dispatchCommand(getServer().getConsoleSender(), String.format("pgs %s %s", name, base));
        }
    }

}
