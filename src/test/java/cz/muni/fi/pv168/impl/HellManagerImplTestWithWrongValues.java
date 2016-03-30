package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.HellManager;
import cz.muni.fi.pv168.common.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;

/**
 * @author Peter Hutta
 * @version 1.0  30.3.2016
 */
public class HellManagerImplTestWithWrongValues {

    private DataSource ds;
    private HellManager manager;

    private static DataSource prepareDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:hellMgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        ds = prepareDataSource();
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript (ds, classLoader.getResource("scripts/createTables.sql"));
        manager = new HellManagerImpl(ds);
    }

    @After
    public void tearDown() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        DBUtils.executeSqlScript(ds, classLoader.getResource("scripts/dropTables.sql"));
    }


}
