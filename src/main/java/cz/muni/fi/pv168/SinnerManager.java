package cz.muni.fi.pv168;

import java.util.List;

/**
 * Interface for sinner manager.
 *
 * @author Peter Hutta
 * @version 1.0  26.2.2016
 */
public interface SinnerManager {

    /**
     * Stores new sinner into database. His Id is generated automatically
     * and stored into id attribute.
     *
     * @param sinner Grave to be created.
     * @throws IllegalArgumentException When sinner is null or when sinner already has id.
     * @throws
     */
    Sinner createSinner(Sinner sinner);

    /**
     * Updates sinner in database.
     *
     * @param sinner Updated sinner to be stored into database.
     * @throws IllegalArgumentException When sinner is null or has null id.
     */
    void updateSinner(Sinner sinner);

    /**
     * Deletes sinner from database.
     *
     * @param sinner Sinner to be deleted.
     * @throws IllegalArgumentException When sinner is null or has null id.
     */
    void deleteSinner(Sinner sinner);

    /**
     * Finds sinner with given id.
     *
     * @param id Id of sought sinner.
     * @return Sinner with given id or null if such sinner does not exist.
     * @throws IllegalArgumentException When given id is null.
     */
    Sinner findSinnerById(Long id);

    /**
     * Finds all sinners in database.
     *
     * @return List of all sinners.
     */
    List<Sinner> findAllSinners();
}
