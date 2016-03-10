package cz.muni.fi.pv168;

import java.util.List;

/**
 * @author Peter Hutta
 * @version 1.0  26.2.2016
 */
public interface SinnerManager {
    void createSinner(Sinner sinner);

    void updateSinner(Sinner sinner);

    void deleteSinner(Sinner sinner);

    Sinner findSinnerById(Long id);

    List<Sinner> findAllSinners();
}
