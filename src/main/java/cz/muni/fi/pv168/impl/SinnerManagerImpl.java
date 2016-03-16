package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.SinnerManager;

import java.util.List;

/**
 * Implementation of sinner manager. Keeps data in memory.
 *
 * @author Peter Hutta
 * @version 1.0  10.3.2016
 */
public class SinnerManagerImpl implements SinnerManager {
    public void createSinner(Sinner sinner) {

    }

    public void updateSinner(Sinner sinner) {

    }

    public void deleteSinner(Sinner sinner) {
        if (sinner == null) {
            throw new IllegalArgumentException("null");
        }
    }

    public Sinner findSinnerById(Long id) {
        return null;
    }

    public List<Sinner> findAllSinners() {
        return null;
    }
}
