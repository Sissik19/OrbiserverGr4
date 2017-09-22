package org.orbisgis.orbisserver.coreserver.utils;

import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.utilities.SFSUtilities;
import org.orbisgis.orbisserver.api.service.Service;
import org.orbisgis.orbisserver.api.service.ServiceFactory;
import org.orbisgis.orbisserver.coreserver.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class implementing the Runnable interface, used to start a session in an other thread and avoid server freeze.
 *
 * @author Sylvain PALOMINOS
 */

public class SessionInitializer implements Runnable {

    /** Logger of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionInitializer.class);
    /** Session to initialize. */
    private Session session;
    /** Map of properties to use. */
    private Map<String, Object> propertyMap;
    /** Unique token of the session. */
    private UUID token;
    /** List of the ServiceFactory tu use. */
    private List<ServiceFactory> serviceFactoryList;

    public SessionInitializer(Session session, Map<String, Object> propertyMap, UUID token,
                              List<ServiceFactory> serviceFactoryList){
        this.session = session;
        this.propertyMap = propertyMap;
        this.token = token;
        this.serviceFactoryList = serviceFactoryList;
    }

    @Override
    public void run() {
        //Creates the session workspace
        File workspaceFolder = new File("workspace", token.toString());
        workspaceFolder.mkdirs();
        session.setWorkspace(workspaceFolder);
        propertyMap.put(ServiceFactory.WORKSPACE_FOLDER_PROP, workspaceFolder);

        //Creates the session ExecutorService
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        session.setExecutorService(executorService);
        propertyMap.put(ServiceFactory.EXECUTOR_SERVICE_PROP, executorService);

        //Creates the session DataSource
        DataSource dataSource = null;
        String dataBaseLocation = new File(workspaceFolder, "h2_db.mv.db").getAbsolutePath();
        try {
            dataSource = SFSUtilities.wrapSpatialDataSource(H2GISDBFactory.createDataSource(dataBaseLocation, true));
        } catch (SQLException e) {
            LOGGER.error("Unable to create the database : \n"+e.getMessage());
        }
        LOGGER.info("Session database started.");
        session.setDataSource(dataSource);
        propertyMap.put(ServiceFactory.DATA_SOURCE_PROP, dataSource);


        //Sets the session with the options
        List<Service> serviceList = new ArrayList<>();
        for(ServiceFactory factory : serviceFactoryList) {
            Service service = factory.createService(propertyMap);
            serviceList.add(service);
            LOGGER.info("Service "+service.getClass().getSimpleName()+" started.");
        }
        session.setServiceList(serviceList);
    }
}