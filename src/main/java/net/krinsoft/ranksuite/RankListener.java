package net.krinsoft.ranksuite;

import org.bukkit.entity.Player;
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
		Player player = event.getPlayer();
		plugin.transfer(player.getName(), player.getUniqueId());
		plugin.login(player.getUniqueId());
	}

	@EventHandler
	void playerQuit(PlayerQuitEvent event) {
		plugin.retire(event.getPlayer().getUniqueId());
	}

}
