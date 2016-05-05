package cz.muni.fi.pv168.gui;

/**
 * @author Peter Hutta
 * @version 1.0  3.5.2016
 */
public class Relation {

    private Long sinnerId;
    private String sinnerName;
    private Long cauldronId;

    public Relation(Long sinnerId, String sinnerName, Long cauldronId) {
        this.sinnerId = sinnerId;
        this.sinnerName = sinnerName;
        this.cauldronId = cauldronId;
    }

    public Long getSinnerId() {
        return sinnerId;
    }

    public String getSinnerName() {
        return sinnerName;
    }

    public Long getCauldronId() {
        return cauldronId;
    }

    public void setSinnerId(Long sinnerId) {
        this.sinnerId = sinnerId;
    }

    public void setSinnerName(String sinnerName) {
        this.sinnerName = sinnerName;
    }

    public void setCauldronId(Long cauldronId) {
        this.cauldronId = cauldronId;
    }
}
