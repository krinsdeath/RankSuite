package net.krinsoft.ranksuite;

import java.util.UUID;

/**
 * @author krinsdeath
 */
public class Leader {
    private final UUID uuid;
    private final int minutes;

    
    public Leader(UUID uuid, int minutes) {
    	   this.uuid = uuid;
           this.minutes = minutes;
    }

    public final int getTimePlayed() {
        return this.minutes;
    }

    public final UUID getUUID() {
        return this.uuid;
    }
}
