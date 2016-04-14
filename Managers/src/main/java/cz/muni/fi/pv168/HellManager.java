package cz.muni.fi.pv168;

import java.util.List;

/**
 * @author Peter Hutta
 * @version 1.0  28.2.2016
 */
public interface HellManager {
    List<Sinner> findSinnersInCauldron(Cauldron cauldron);

    Cauldron findCauldronWithSinner(Sinner sinner);

    void boilSinnerInCauldron(Sinner sinner, Cauldron cauldron);

    void releaseSinnerFromCauldron(Sinner sinner);
}
