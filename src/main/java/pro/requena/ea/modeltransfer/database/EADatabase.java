package pro.requena.ea.modeltransfer.database;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pro.requena.ea.modeltransfer.exceptions.EAModelTransferException;

/**
 * Entry-point class that handles model transfer requests.
 * 
 * @author krequena
 * @since 0.1
 */
public class EADatabase {

    private static final String PREFIX_JDBC = "jdbc:";
    private static final int LIMIT_JDBC = 30;

    private static final Logger LOG = LogManager.getLogger();

    /**
     * Create a new database connection to a given EAP/DSN.
     * @param endpoint Endpoint. Can be an EAP file or a DSN specification.
     * @return Opened {@link java.sql.Connection} to the given database.
     * @throws EAModelTransferException Generic handled exception.
     */
    public static final Connection connect(final String endpoint) throws EAModelTransferException {
        if(endpoint.toLowerCase().startsWith(PREFIX_JDBC)) {
            return connectDsn(endpoint);
        } else {
            return connectEap(endpoint);
        }
    }

    /**
     * Creates a JDBC connection to an EAP file.
     * @param eapFilePath EAP file path.
     * @return Connection.
     * @throws EAModelTransferException Generic handled exception.
     */
    private static final Connection connectEap(final String eapFilePath) throws EAModelTransferException {
        try {
            LOG.info("Connecting to EAP file: {}", eapFilePath);
            return DriverManager.getConnection("jdbc:ucanaccess://" + eapFilePath + ";memory=false;immediatelyReleaseResources=true");
        } catch (Exception e) {
            LOG.error("Couldn't open the EAP file connection.");
            throw new EAModelTransferException(e);
        }
    }

    /**
     * Creates a JDBC connection by using a DSN.
     * Caution: first {@link LIMIT_JDBC} characters are printed on log.
     * 
     * @param dsn JDBC DSN.
     * @return Connection.
     * @throws EAModelTransferException Generic handled exception.
     */
    private static final Connection connectDsn(final String dsn) throws EAModelTransferException {
        try {
            LOG.info("Connecting to DB using DSN: {}...", StringUtils.substring(dsn, 0, LIMIT_JDBC));
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(dsn);
        } catch (Exception e) {
            throw new EAModelTransferException(e);
        }
    }

    /**
     * Closes a database connection.
     * @param connection Existing JDBC connection.
     * @throws EAModelTransferException Generic handled exception.
     */
    public static final void close(final Connection connection) throws EAModelTransferException {
        try {
            LOG.info("Closing connection.");
            connection.close();
        } catch (Exception e) {
            throw new EAModelTransferException(e);
        }
    }
}
