package cz.muni.fi.pv168.web;

import cz.muni.fi.pv168.Cauldron;
import cz.muni.fi.pv168.CauldronManager;
import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.SinnerManager;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Peter Hutta
 * @version 1.0  19.4.2016
 */
@WebServlet(ServiceServlet.MAPPING + "/*")
public class ServiceServlet extends HttpServlet {

    private static final String LIST_JSP = "/list.jsp";
    public static final String MAPPING = "/list_all";
    public static final String CAULDRON_MAPPING = "/cauldrons";
    public static final String SINNER_MAPPING = "/sinners";

    @Override
    protected void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        listAllEntities(servletRequest, servletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        servletRequest.setCharacterEncoding("utf-8");
        String path = servletRequest.getPathInfo();
        String entity = path.substring(0, path.indexOf('/', path.indexOf('/') + 1));
        String action = path.substring(path.indexOf('/', path.indexOf('/') + 1));

        switch (entity) {
            case "/cauldrons":
                cauldronService(action, servletRequest, servletResponse);
                break;

            case "/sinners":
                sinnerService(action, servletRequest, servletResponse);
                break;

            default:
                servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown entity " + entity);
                break;
        }
    }

    private SinnerManager getSinnerManager() {
        return (SinnerManager) getServletContext().getAttribute("sinnerManager");
    }

    private CauldronManager getCauldronManager() {
        return (CauldronManager) getServletContext().getAttribute("cauldronManager");
    }

    private void listAllEntities(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        try {
            servletRequest.setAttribute("cauldrons", getCauldronManager().findAllCauldrons());
            servletRequest.setAttribute("sinners", getSinnerManager().findAllSinners());
            servletRequest.getRequestDispatcher(LIST_JSP).forward(servletRequest, servletResponse);
        }
        catch (ServiceFailureException e) {
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void cauldronService(String action, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        switch (action) {
            case "/add":
                addCauldron(servletRequest, servletResponse);
                break;

            case "/delete":
                deleteCauldron(servletRequest, servletResponse);
                break;

            case "/preupdate":
                servletRequest.setAttribute("cauldronId", servletRequest.getParameter("id"));
                listAllEntities(servletRequest, servletResponse);
                break;

            case "/update":
                updateCauldron(servletRequest, servletResponse);
                break;

            default:
                servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
                break;
        }
    }

    private void addCauldron(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        try {
            Cauldron cauldron = createCauldron(servletRequest, servletResponse);

            getCauldronManager().createCauldron(cauldron);
            servletResponse.sendRedirect(servletRequest.getContextPath()+MAPPING);
        }
        catch (NumberFormatException e) {
            servletRequest.setAttribute("cauldronError", "Error: Cannot parse given values.");
            listAllEntities(servletRequest, servletResponse);
        }
        catch (ServiceFailureException e) {
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void deleteCauldron(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        try {
            Long id = Long.valueOf(servletRequest.getParameter("id"));
            getCauldronManager().deleteCauldron(getCauldronManager().findCauldronById(id));
            servletResponse.sendRedirect(servletRequest.getContextPath() + MAPPING);
        }
        catch (ServiceFailureException e) {
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void updateCauldron(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        try {
            Cauldron cauldron = createCauldron(servletRequest, servletResponse);
            cauldron.setId(Long.valueOf(servletRequest.getParameter("id")));

            getCauldronManager().updateCauldron(cauldron);
            servletResponse.sendRedirect(servletRequest.getContextPath()+MAPPING);
        }
        catch (NumberFormatException e) {
            servletRequest.setAttribute("cauldronError", "Error: Cannot parse given values.");
            listAllEntities(servletRequest, servletResponse);
        }
        catch (ServiceFailureException e) {
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void sinnerService(String action, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        switch (action) {
            case "/add":
                addSinner(servletRequest, servletResponse);
                break;

            case "/delete":
                deleteSinner(servletRequest, servletResponse);
                break;

            case "/preupdate":
                servletRequest.setAttribute("sinnerId", servletRequest.getParameter("id"));
                listAllEntities(servletRequest, servletResponse);
                break;

            case "/update":
                updateSinner(servletRequest, servletResponse);
                break;

            default:
                servletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
                break;
        }
    }

    private void addSinner(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        try {
            Sinner sinner = createSinner(servletRequest, servletResponse);

            getSinnerManager().createSinner(sinner);
            servletResponse.sendRedirect(servletRequest.getContextPath()+MAPPING);
        }
        catch (NumberFormatException e) {
            servletRequest.setAttribute("sinnerError", "Error: Cannot parse given values.");
            listAllEntities(servletRequest, servletResponse);
        }
        catch (ServiceFailureException e) {
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void deleteSinner(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        try {
            Long id = Long.valueOf(servletRequest.getParameter("id"));
            getSinnerManager().deleteSinner(getSinnerManager().findSinnerById(id));
            servletResponse.sendRedirect(servletRequest.getContextPath() + ServiceServlet.MAPPING);
        }
        catch (ServiceFailureException e) {
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void updateSinner(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        try {
            Sinner sinner = createSinner(servletRequest, servletResponse);
            sinner.setId(Long.valueOf(servletRequest.getParameter("id")));

            getSinnerManager().updateSinner(sinner);
            servletResponse.sendRedirect(servletRequest.getContextPath()+MAPPING);
        }
        catch (NumberFormatException e) {
            servletRequest.setAttribute("sinnerError", "Error: Cannot parse given values.");
            listAllEntities(servletRequest, servletResponse);
        }
        catch (ServiceFailureException e) {
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Cauldron createCauldron(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        int capacity = Integer.parseInt(servletRequest.getParameter("capacity"));
        int temperature = Integer.parseInt(servletRequest.getParameter("waterTemperature"));
        int hellFloor = Integer.parseInt(servletRequest.getParameter("hellFloor"));

        if (capacity <= 0 || hellFloor < 0) {
            servletRequest.setAttribute("cauldronError", "Error: Invalid values.");
            listAllEntities(servletRequest, servletResponse);
        }

        Cauldron cauldron = new Cauldron();
        cauldron.setCapacity(capacity);
        cauldron.setWaterTemperature(temperature);
        cauldron.setHellFloor(hellFloor);
        return cauldron;
    }

    private Sinner createSinner(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        String firstName = servletRequest.getParameter("firstName");
        String lastName = servletRequest.getParameter("lastName");
        String sin = servletRequest.getParameter("sin");

        String date = servletRequest.getParameter("releaseDate");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = null;
        if (!date.isEmpty()) {
            releaseDate = LocalDate.parse(servletRequest.getParameter("releaseDate"), formatter);
        }

        boolean signedContractWithDevil = false;
        if (servletRequest.getParameter("signedContractWithDevil") != null) {
            signedContractWithDevil = true;
        }

        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || sin == null ||
                (releaseDate == null && !signedContractWithDevil) || (releaseDate != null && signedContractWithDevil)) {
            servletRequest.setAttribute("sinnerError", "Error: Invalid values.");
            listAllEntities(servletRequest, servletResponse);
        }

        Sinner sinner = new Sinner();
        sinner.setFirstName(firstName);
        sinner.setLastName(lastName);
        sinner.setSin(sin);
        sinner.setReleaseDate(releaseDate);
        sinner.setSignedContractWithDevil(signedContractWithDevil);
        return sinner;
    }
}
