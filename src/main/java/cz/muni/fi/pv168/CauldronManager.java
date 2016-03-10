package cz.muni.fi.pv168;

import java.util.List;

/**
 * @author Peter Hutta
 * @version 1.0  26.2.2016
 */
public interface CauldronManager {
    void createCauldron(Cauldron cauldron);

    void updateCauldron(Cauldron cauldron);

    void deleteCauldron(Cauldron cauldron);

    Cauldron findCauldronById(Long id);

    List<Cauldron> findAllCauldrons();
}
