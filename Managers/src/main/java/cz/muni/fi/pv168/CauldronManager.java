package cz.muni.fi.pv168;

import java.util.List;

/**
 * @author Peter Hutta
 * @version 1.0  26.2.2016
 */
public interface CauldronManager {

    /**
     * Stores new cauldron into database. Its Id is generated automatically
     * and stored into id attribute.
     *
     * @param cauldron Cauldron to be created.
     * @throws IllegalArgumentException When cauldron capacity is lower than 1 or hell floor lower than 0
     * or null is inserted
     */
    void createCauldron(Cauldron cauldron);

    /**
     * Update a cauldron in database.
     * @param cauldron Cauldron to be created.
     * @throws IllegalArgumentException When null for cauldron is inserted or capacity once set has be changed
     * or when Id is set null or changing Id that was once set or hell floor has to be changed to numbers lower than 0
     */
    void updateCauldron(Cauldron cauldron);

    /**
     * Delete a cauldron from database.
     *
     * @param cauldron Cauldron to be created.
     * @throws IllegalArgumentException When null for cauldron or null id value in Cauldron is inserted
     */
    void deleteCauldron(Cauldron cauldron);

    Cauldron findCauldronById(Long id);

    List<Cauldron> findAllCauldrons();
}
