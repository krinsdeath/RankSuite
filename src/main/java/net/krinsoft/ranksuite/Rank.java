package net.krinsoft.ranksuite;

/**
 * @author krinsdeath
 */
public class Rank {

    private String name;
    private String next;
    private int minutes;
    private String message;

    public Rank(String n, String to, int min, String msg) {
        this.name = n;
        this.next = to;
        this.minutes = min;
        this.message = msg;
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
