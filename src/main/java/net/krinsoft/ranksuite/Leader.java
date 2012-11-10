package net.krinsoft.ranksuite;

/**
 * @author krinsdeath
 */
public class Leader {
    private final String name;
    private final int minutes;

    public Leader(String name, int minutes) {
        this.name = name;
        this.minutes = minutes;
    }

    public final int getTimePlayed() {
        return this.minutes;
    }

    public final String getName() {
        return this.name;
    }
}
