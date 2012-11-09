package net.krinsoft.ranksuite;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author krinsdeath
 */
public class RankListener implements Listener {

    private RankCore plugin;

    public RankListener(RankCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    void playerJoin(PlayerJoinEvent event) {
        plugin.login(event.getPlayer().getName());
    }

    @EventHandler
    void playerQuit(PlayerQuitEvent event) {
        plugin.retire(event.getPlayer().getName());
    }

}
