package net.krinsoft.ranksuite.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author krinsdeath
 */
public class RankSuiteRankResetEvent extends Event {
    private final static HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final String player;
    private final String base;

    public RankSuiteRankResetEvent(final String player, final String base) {
        this.player = player;
        this.base = base;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getPlayer() {
        return this.player;
    }

    public String getBaseRank() {
        return this.base;
    }
}
