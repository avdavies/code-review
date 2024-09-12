package uk.co.autoventive.ymscachelambda.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import uk.co.autoventive.ymscachelambda.exception.MissingEnviromentVariableException;
import uk.co.autoventive.ymscachelambda.model.GeoFence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class CacheUpdateService {
    private final String[] REQUIRED_VARIABLES = {"REDIS_HOST", "REDIS_PORT"};

    public void updateCache(List<GeoFence> geoFences) throws MissingEnviromentVariableException {

        checkForEnvironmentVariablesExist();

        log.info("Creating Jedis client");
        String host = System.getenv("REDIS_HOST");
        String portAsString = System.getenv("REDIS_PORT");
        int port = Integer.parseInt(portAsString);

        ArrayList<String> foundIds = new ArrayList<>();

        try (Jedis jedis = new Jedis(host, port, true)) {
            log.info("Client created");
            log.info("GeoFence count: {}", geoFences.size());
            for (GeoFence geoFence : geoFences) {
                String key = String.valueOf(geoFence.getId());
                if (jedis.exists(key)) {
                    log.debug("Found existing key {}", key);
                } else {
                    log.debug("Creating new entry for {}", key);
                }
                log.info("Saving {}", key);
                jedis.set(key, geoFence.getGeoJson());
                foundIds.add(key);
            }
            log.info("Cache updated");

            String cursor = ScanParams.SCAN_POINTER_START;
            // Find all keys
            ScanParams scanParams = new ScanParams().match("*").count(1000);

            ArrayList<String> idsMissing = new ArrayList<>();
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                cursor = scanResult.getCursor();  // Update the cursor for the next iteration
                for (String key : scanResult.getResult()) {
                      if (!foundIds.contains(key)) {
                          idsMissing.add(key);
                      }
                }
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));

            for (String missingKey : idsMissing) {
                jedis.del(missingKey);
                log.info("Deleted {}", missingKey);
            }
        }
    }

    private void checkForEnvironmentVariablesExist() throws MissingEnviromentVariableException {
        for (String variable : REQUIRED_VARIABLES) {
            if (System.getenv(variable) == null) {
                throw new MissingEnviromentVariableException("Missing " + variable);
            }
        }
    }
}
