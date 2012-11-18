package net.krinsoft.ranksuite;

/**
 * @author krinsdeath
 */
public class Rank {

    private final String name;
    private final String next;
    private final int minutes;
    private final String message;

    public Rank(String n, String to, int min, String msg) {
        this.name = n;
        this.next = to;
        this.minutes = min;
        this.message = msg.replace("%r", this.name);
    }

    public String getName() {
        return this.name;
    }

    public String getNextRank() {
        return this.next;
    }

    public int getMinutesRequired() {
        return this.minutes;
    }

    public String getPromotionMessage() {
        return this.message;
    }

}
