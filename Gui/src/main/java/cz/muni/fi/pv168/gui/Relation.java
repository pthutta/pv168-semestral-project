package cz.muni.fi.pv168.gui;

/**
 * @author Peter Hutta
 * @version 1.0  3.5.2016
 */
public class Relation {

    private long sinnerId;
    private String sinnerName;
    private long cauldronId;

    public Relation(long sinnerId, String sinnerName, long cauldronId) {
        this.sinnerId = sinnerId;
        this.sinnerName = sinnerName;
        this.cauldronId = cauldronId;
    }

    public long getSinnerId() {
        return sinnerId;
    }

    public String getSinnerName() {
        return sinnerName;
    }

    public long getCauldronId() {
        return cauldronId;
    }
}
