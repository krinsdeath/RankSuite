package net.krinsoft.ranksuite.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author krinsdeath
 */
public class RankSuiteRankChangeEvent extends Event {
    private final static HandlerList handlers = new HandlerList();
    private final String player;
    private final String old;
    private final String rank;

    public RankSuiteRankChangeEvent(String player, String old, String rank) {
        this.player = player;
        this.old = old;
        this.rank = rank;
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

    public String getOldRank() {
        return this.old;
    }

    public String getNewRank() {
        return this.rank;
    }
}
