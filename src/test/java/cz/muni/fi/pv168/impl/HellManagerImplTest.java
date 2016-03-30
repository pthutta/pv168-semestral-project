package cz.muni.fi.pv168.impl;

import cz.muni.fi.pv168.HellManager;
import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.common.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Comparator;

/**
 * @author Peter Hutta
 * @version 1.0  30.3.2016
 */
public class HellManagerImplTest {

    private DataSource ds;
    private HellManager manager;

    private static DataSource prepareDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:hellMgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

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



    private static Comparator<Sinner> sinnerComparator = (o1, o2) -> o1.getId().compareTo(o2.getId());
}
