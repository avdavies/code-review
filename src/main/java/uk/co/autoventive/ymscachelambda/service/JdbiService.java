package uk.co.autoventive.ymscachelambda.service;

import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import uk.co.autoventive.ymscachelambda.exception.MissingEnviromentVariableException;

@Slf4j
public class JdbiService {
    private final String DB_USERNAME = "YMS_DB_USERNAME";
    private final String DB_PASSWORD = "YMS_DB_PASSWORD";
    private final String DB_URL = "YMS_DB_URL";
    private final String DB_NAME = "YMS_DB_NAME";
    private final String DB_PORT = "YMS_DB_PORT";

    private final String[] REQUIRED_VARIABLES = {
            DB_USERNAME,
            DB_PASSWORD,
            DB_URL,
            DB_NAME,
            DB_PORT};

    public Jdbi getConnection() throws JdbiException {
        try {
            checkForEnvironmentVariablesExist();
        } catch (MissingEnviromentVariableException e) {
            throw new RuntimeException(e);
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            log.error("No Postgres driver installed", e);
            return null;
        }

        // Will throw Jdbi Exception
        return Jdbi.create(System.getenv(DB_URL) + ":" + System.getenv(DB_PORT) + "/" + System.getenv(DB_NAME), System.getenv(DB_USERNAME), System.getenv(DB_PASSWORD));
    }

    private void checkForEnvironmentVariablesExist() throws MissingEnviromentVariableException {
        for (String variable : REQUIRED_VARIABLES) {
            if (System.getenv(variable) == null) {
                throw new MissingEnviromentVariableException("Missing " + variable);
            }
        }
    }
}
