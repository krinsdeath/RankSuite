package net.krinsoft.ranksuite;

/**
 * @author krinsdeath
 */
public class RankedPlayer {

    private RankCore plugin;
    private String name;
    private Rank current;
    private double minutes;
    private long login;
    private boolean exempt;

    public RankedPlayer(RankCore plugin, String name, Rank rank, int minutes, long login, boolean exempt) {
        this.plugin = plugin;
        this.name = name;
        this.current = rank;
        this.minutes = minutes;
        this.login = login;
        this.exempt = exempt;
    }

    public Rank getRank() {
        return this.current;
    }

    public void setRank(Rank rank) {
        this.current = rank;
    }

    public int getTimePlayed() {
        return (int) this.minutes;
    }

    public boolean addTime() {
        long time = System.currentTimeMillis();
        this.minutes += ((time - this.login) / 1000 / 60);
        this.login = time;
        if (this.minutes > 0) {
            plugin.getDB().set(this.name.toLowerCase(), (int) this.minutes);
        }
        if (!exempt && this.current.getNextRank() != null && !this.current.getNextRank().equals("none")) {
            Rank next = plugin.getRank(this.current.getNextRank());
            if (next == null) {
                plugin.debug("Next rank '" + this.current.getNextRank() + "' for '" + this.name + "' is null! Is there a mistake in 'config.yml'?");
                return false;
            }
            if (next.getMinutesRequired() <= this.minutes) {
                return true;
            }
        }
        return false;
    }

    public void addTime(int mins) {
        this.minutes += mins;
        addTime();
    }

    public void removeTime(int mins) {
        this.minutes -= mins;
        if (this.minutes < 0) { this.minutes = 0; }
        addTime();
    }

    public void reset() {
        this.minutes = 0;
        this.login = System.currentTimeMillis();
        addTime();
    }

}
