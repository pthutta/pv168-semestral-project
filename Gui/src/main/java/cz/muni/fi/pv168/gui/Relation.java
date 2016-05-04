package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.Cauldron;
import cz.muni.fi.pv168.Sinner;

/**
 * @author Peter Hutta
 * @version 1.0  3.5.2016
 */
public class Relation {

    private Sinner sinner;
    private Cauldron cauldron;

    public Relation(Sinner sinner, Cauldron cauldron) {
        this.sinner = sinner;
        this.cauldron = cauldron;
    }

    public long getSinnerId() {
        return sinner.getId();
    }

    public String getFullName() {
        return sinner.getFirstName() + " " + sinner.getLastName();
    }

    public long getCauldronId() {
        return cauldron.getId();
    }
}
