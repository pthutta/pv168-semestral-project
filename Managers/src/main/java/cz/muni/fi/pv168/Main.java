package cz.muni.fi.pv168;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;

import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * @author Peter Hutta
 * @version 1.0  14.4.2016
 */
public class Main {

    public static DataSource createMemoryDatabase() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(EmbeddedDriver.class.getName());
        dataSource.setUrl("jdbc:derby:memory:hellDB;create=true");

        new ResourceDatabasePopulator( new ClassPathResource("scripts/createTables.sql"),
                new ClassPathResource("scripts/fillTables.sql")).execute(dataSource);
        return dataSource;
    }

}
