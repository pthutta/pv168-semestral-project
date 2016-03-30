package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.Cauldron;
import cz.muni.fi.pv168.CauldronManager;
import cz.muni.fi.pv168.common.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by David Čechák on 13.03.2016.
 */
public class CauldronManagerImplTest {

    private DataSource ds;
    private CauldronManagerImpl manager;

    private static DataSource prepareDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:cauldronMgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void SetUp() throws SQLException {
        ds = prepareDataSource();
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript (ds, classLoader.getResource("scripts/createTables.sql"));
        manager = new CauldronManagerImpl(ds);
    }

    @After
    public void tearDown() throws SQLException {
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript(ds, classLoader.getResource("scripts/dropTables.sql"));
    }

    //createCauldron tests
    @Test
    public void createCauldronWithRightValues(){
        Cauldron cauldron = newCauldron(1, 3, 120);
        manager.createCauldron(cauldron);

        Long cauldronId = cauldron.getId();
        assertNotNull("Newly saved Cauldron has null ID", cauldronId);

        Cauldron loaded = manager.findCauldronById(cauldronId);
        assertThat("Loaded cauldron differs from the created one", loaded, is(equalTo(cauldron)));
        assertThat("Loaded cauldron is the same instance", loaded, is(not(sameInstance(cauldron))));
    }

    @Test
    public void createCauldronWithWrongValues() {
        Cauldron cauldron = newCauldron(4, -2, 200);

        try {
            manager.createCauldron(cauldron);
            fail("cauldron with capacity lower than 1 not detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        cauldron = newCauldron(-100, 2, 200);
        try {
            manager.createCauldron(cauldron);
            fail("cauldron with hell floor lower than 0 not detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            manager.createCauldron(null);
            fail("null parameter for createCauldron not detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    //delete Cauldron tests
    @Test
    public void deleteCauldronWithRightValues(){
        Cauldron cauldron = newCauldron(2, 5, 250);
        manager.createCauldron(cauldron);

        assertNotNull(manager.findCauldronById(cauldron.getId()));

        manager.deleteCauldron(cauldron);

        assertNull(manager.findCauldronById(cauldron.getId()));
    }

    @Test
    public void deleteCauldronWithWrongValues() {

        Cauldron cauldron = newCauldron(2, 5, 1000);

        try {
            manager.deleteCauldron(null);
            fail("null parameter for deleteCauldron not detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            cauldron.setId(null);
            manager.deleteCauldron(cauldron);
            fail("null id value in Cauldron not detected while calling deleteCauldron");
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    //update tests
    @Test
    public void updateCauldron() {
        Cauldron cauldron1 = newCauldron(2, 5, 250);
        Cauldron cauldron2 = newCauldron(2, 5, 250);
        manager.createCauldron(cauldron1);
        manager.createCauldron(cauldron2);

        Long cauldron1Id = cauldron1.getId();

        //change HellFloor
        cauldron1.setHellFloor(1);
        manager.updateCauldron(cauldron1);
        //load from database
        cauldron1 = manager.findCauldronById(cauldron1Id);
        assertEquals(1, cauldron1.getHellFloor());
        assertEquals(5, cauldron1.getCapacity());
        assertEquals(250, cauldron1.getWaterTemperature());

        //change water temperature
        cauldron1 = newCauldron(2, 5, 250);
        manager.createCauldron(cauldron1);
        cauldron1Id = cauldron1.getId();

        cauldron1.setWaterTemperature(9999);
        manager.updateCauldron(cauldron1);
        //load from database
        cauldron1 = manager.findCauldronById(cauldron1Id);
        assertEquals(2, cauldron1.getHellFloor());
        assertEquals(5, cauldron1.getCapacity());
        assertEquals(9999, cauldron1.getWaterTemperature());
    }


    @Test
    public void updateGraveWithWrongAttributes() {

        Cauldron cauldron1 = newCauldron(2, 5, 250);
        manager.createCauldron(cauldron1);

        Long cauldron1Id = cauldron1.getId();

        try {
            manager.updateCauldron(null);
            fail("null parameter for updateCauldron not detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        /*try {
            cauldron1.setCapacity(2);
            manager.updateCauldron(cauldron1);
            fail("capacity once set cannot be changed");
        } catch (IllegalArgumentException ex) {
            //OK
        }*/

        try {
            cauldron1 = manager.findCauldronById(cauldron1Id);
            cauldron1.setId(null);
            manager.updateCauldron(cauldron1);
            fail("setId with null parameter not detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            cauldron1.setHellFloor(-5);
            manager.updateCauldron(cauldron1);
            fail("hell floor cannot be set to numbers lower than 0");
        } catch (IllegalArgumentException ex) {
            //OK
        }


    }

    //findAllCauldrons test
    @Test
    public void findAllCauldronsComparison() {

        assertTrue(manager.findAllCauldrons().isEmpty());

        Cauldron c1 = newCauldron(2, 5, 250);
        Cauldron c2 = newCauldron(5, 1, 9999);

        manager.createCauldron(c1);
        manager.createCauldron(c2);

        List<Cauldron> expected = Arrays.asList(c1, c2);
        List<Cauldron> actual = manager.findAllCauldrons();

        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals("Cauldrons in database differs from the cauldrons saved into database before", expected, actual);
    }



    private static Cauldron newCauldron(int floor, int capacity, int temperature) {
        Cauldron cauldron = new Cauldron();
        cauldron.setHellFloor(floor);
        cauldron.setCapacity(capacity);
        cauldron.setWaterTemperature(temperature);
        return cauldron;
    }

    private static Comparator<Cauldron> idComparator = (c1, c2) -> c1.getId().compareTo(c2.getId());
}
