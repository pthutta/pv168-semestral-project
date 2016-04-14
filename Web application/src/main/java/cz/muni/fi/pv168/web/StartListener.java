package cz.muni.fi.pv168.web;

import cz.muni.fi.pv168.Main;
import cz.muni.fi.pv168.impl.CauldronManagerImpl;
import cz.muni.fi.pv168.impl.HellManagerImpl;
import cz.muni.fi.pv168.impl.SinnerManagerImpl;

import javax.sql.DataSource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.Clock;

/**
 * @author Peter Hutta
 * @version 1.0  14.4.2016
 */
@WebListener
public class StartListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        System.out.println("aplikace inicializována");
        ServletContext servletContext = ev.getServletContext();
        DataSource dataSource = Main.createMemoryDatabase();

        servletContext.setAttribute("cauldronManager", new CauldronManagerImpl(dataSource));
        servletContext.setAttribute("hellManager", new HellManagerImpl(dataSource));
        servletContext.setAttribute("sinnerManager", new SinnerManagerImpl(dataSource, Clock.systemDefaultZone()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        System.out.println("aplikace končí");
    }
}